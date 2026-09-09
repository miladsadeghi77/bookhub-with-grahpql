package com.miladsadeghi.bookhubwithgraphql.domain.usecase;

import com.miladsadeghi.bookhubwithgraphql.api.dto.BookDto;
import com.miladsadeghi.bookhubwithgraphql.api.error.ErrorCode;
import com.miladsadeghi.bookhubwithgraphql.api.error.UserError;
import com.miladsadeghi.bookhubwithgraphql.api.input.BookFilter;
import com.miladsadeghi.bookhubwithgraphql.api.input.BookSortField;
import com.miladsadeghi.bookhubwithgraphql.api.input.CreateBookInput;
import com.miladsadeghi.bookhubwithgraphql.api.input.RetrieveBookInput;
import com.miladsadeghi.bookhubwithgraphql.api.input.SortDirection;
import com.miladsadeghi.bookhubwithgraphql.api.payload.BookPayload;
import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.entity.Author;
import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.entity.Book;
import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.entity.Publisher;
import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.repository.AuthorRepository;
import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.repository.BookRepository;
import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.repository.PublisherRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.graphql.data.query.ScrollSubrange;
import org.springframework.stereotype.Service;

@Service
public class BookUseCase {

  private final BookRepository bookRepository;
  private final AuthorRepository authorRepository;
  private final PublisherRepository publisherRepository;
  private final Validator validator;

  public BookUseCase(BookRepository bookRepository, AuthorRepository authorRepository,
      PublisherRepository publisherRepository, Validator validator) {
    this.bookRepository = bookRepository;
    this.authorRepository = authorRepository;
    this.publisherRepository = publisherRepository;
    this.validator = validator;
  }


  public BookPayload book(RetrieveBookInput bookInput) {
    Set<ConstraintViolation<RetrieveBookInput>> violations = validator.validate(bookInput);
    List<UserError> errors = violations.stream()
        .map(v -> new UserError(
            v.getMessage(),
            extractFieldName(v),
            ErrorCode.INVALID_INPUT
        ))
        .collect(Collectors.toCollection(ArrayList::new));

    if (!errors.isEmpty()) {
      return new BookPayload(null, errors);
    }

    Optional<Book> book = bookRepository.findById(Long.parseLong(bookInput.id()));
    return toPayload(book);
  }

  public Window<BookDto> books( BookFilter filter,
      BookSortField sortField,
      SortDirection sortDirection,
      ScrollSubrange subrange) {

    ScrollPosition position = subrange.position().orElse(ScrollPosition.keyset());
    int count = subrange.count().orElse(3);

    Sort sort = getSortedBook(sortField, sortDirection);
    Specification<Book> spec = getBookSpecification(filter);

    Window<Book> window = bookRepository.findBy(
        spec,
        q -> q.sortBy(sort).limit(count).scroll(position)
    );
    return window.map(this::toDto);
  }

  public BookPayload createBook(CreateBookInput input) {
    Set<ConstraintViolation<CreateBookInput>> violations = validator.validate(input);
    List<UserError> errors = violations.stream()
        .map(v -> new UserError(
            v.getMessage(),
            extractFieldName(v),
            ErrorCode.INVALID_INPUT
        ))
        .collect(Collectors.toCollection(ArrayList::new));

    if (!errors.isEmpty()) {
      return new BookPayload(null, errors);
    }

    Author author = authorRepository.findById(input.authorId()).orElse(null);
    if (author == null) {
      errors.add(new UserError("Author not found", "authorId", ErrorCode.NOT_FOUND));
    }
    if (input.publisherId() != null) {
      Publisher publisher = publisherRepository.findById(input.publisherId()).orElse(null);
      if (publisher == null) {
        errors.add(new UserError("Publisher not found", "publisherId", ErrorCode.NOT_FOUND));
      }
    }

    if (!errors.isEmpty()) {
      return new BookPayload(null, errors);
    }

    Book book = new Book();
    book.setTitle(input.title());
    book.setAuthor(author);
    book.setPublishedYear(input.publishedYear());

    if (input.publisherId() != null) {
      Publisher publisher = publisherRepository.findById(input.publisherId()).orElse(null);
      book.setPublisher(publisher);
    }
    boolean exists = bookRepository.existsByTitleAndAuthorIdAndPublisherId(input.title(),
        input.authorId(), input.publisherId());

    if (exists) {
      errors.add(new UserError("Book already exists", "title", ErrorCode.DUPLICATE));
      return new BookPayload(null, errors);
    }
    try {
      Book saved = bookRepository.save(book);
      return new BookPayload(toDto(saved), List.of());
    } catch (DataIntegrityViolationException e) {
      errors.add(new UserError("Book already exists", "title", ErrorCode.DUPLICATE));
      return new BookPayload(null, errors);
    }

  }


  public Map<BookDto, Author> author(List<BookDto> books) {
    List<Long> authorIds = books.stream()
        .map(BookDto::authorId)
        .distinct()
        .toList();

    Map<Long, Author> authorById = authorRepository.findAllById(authorIds).stream()
        .collect(Collectors.toMap(Author::getId, a -> a));

    return books.stream()
        .collect(Collectors.toMap(
            bookDto -> bookDto,
            bookDto -> authorById.get(bookDto.authorId())
        ));
  }

  public Map<BookDto, Publisher> publisher(List<BookDto> books) {
    List<Long> publisherIds = books.stream()
        .filter(book -> book.publisherId() != null)
        .map(BookDto::publisherId)
        .distinct()
        .toList();

    Map<Long, Publisher> publisherById = publisherRepository.findAllById(publisherIds).stream()
        .collect(Collectors.toMap(Publisher::getId, a -> a));

    return books.stream()
        .filter(book -> book.publisherId() != null)
        .collect(Collectors.toMap(
            book -> book,
            book -> publisherById.get(book.publisherId())
        ));
  }


  private BookDto toDto(Book book) {
    Long publisherID = book.getPublisher() != null ? book.getPublisher().getId() : null;
    return new BookDto(book.getId(), book.getTitle(), book.getPublishedYear(),
        book.getAuthor().getId(), publisherID);
  }

  private BookPayload toPayload(Optional<Book> bookOptional) {
    List<UserError> errors = new ArrayList<>();

    if (bookOptional.isEmpty()) {
      errors.add(new UserError("Book not found", "bookId", ErrorCode.NOT_FOUND));
      return new BookPayload(null, errors);
    }
    Book book = bookOptional.get();
    Long publisherID = book.getPublisher() != null ? book.getPublisher().getId() : null;
    BookDto bookDto = new BookDto(book.getId(), book.getTitle(), book.getPublishedYear(),
        book.getAuthor().getId(), publisherID);
    return new BookPayload(bookDto, errors);
  }

  private String extractFieldName(ConstraintViolation<?> violation) {
    String path = violation.getPropertyPath().toString();
    return path.substring(path.lastIndexOf('.') + 1);
  }


  private static Specification<Book> getBookSpecification(BookFilter filter) {
    Specification<Book> spec = Specification.unrestricted();

    if (filter != null && filter.titleContains() != null) {
      spec = spec.and((root, query, cb) ->
          cb.like(cb.lower(root.get("title")), "%" + filter.titleContains().toLowerCase() + "%"));
    }

    if (filter != null && filter.authorId() != null) {
      spec = spec.and((root, query, cb) ->
          cb.equal(root.get("author").get("id"), filter.authorId()));
    }
    return spec;
  }

  private static Sort getSortedBook(BookSortField sortField, SortDirection sortDirection) {
    Sort sort = switch (sortField != null ? sortField : BookSortField.TITLE) {
      case TITLE -> Sort.by("title");
      case PUBLISHED_YEAR -> Sort.by("publishedYear");
    };

    sort = sortDirection == SortDirection.DESC ? sort.descending() : sort.ascending();
    return sort.and(Sort.by("id"));
  }

}


