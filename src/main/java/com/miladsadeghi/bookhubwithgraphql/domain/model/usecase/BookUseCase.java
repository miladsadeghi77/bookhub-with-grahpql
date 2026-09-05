package com.miladsadeghi.bookhubwithgraphql.domain.model.usecase;

import com.miladsadeghi.bookhubwithgraphql.api.dto.BookDto;
import com.miladsadeghi.bookhubwithgraphql.api.error.ErrorCode;
import com.miladsadeghi.bookhubwithgraphql.api.error.UserError;
import com.miladsadeghi.bookhubwithgraphql.api.input.CreateBookInput;
import com.miladsadeghi.bookhubwithgraphql.api.payload.BookPayload;
import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.entity.Author;
import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.entity.Book;
import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.entity.Publisher;
import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.repository.AuthorRepository;
import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.repository.BookRepository;
import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.repository.PublisherRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class BookUseCase {
  private final BookRepository bookRepository;
  private final AuthorRepository authorRepository;
  private final PublisherRepository publisherRepository;

  public BookUseCase(BookRepository bookRepository, AuthorRepository authorRepository,
      PublisherRepository publisherRepository) {
    this.bookRepository = bookRepository;
    this.authorRepository = authorRepository;
    this.publisherRepository = publisherRepository;
  }



  public BookPayload book(String id) {
    Optional<Book> book = bookRepository.findById(Long.parseLong(id));
    return toPayload(book);
  }


  public List<BookDto> books() {

    return bookRepository.findAll().stream()
        .map(book -> new BookDto(book.getId(), book.getTitle(),
            book.getPublishedYear(),
            book.getAuthor().getId(),
            book.getPublisher() != null ? book.getPublisher().getId() : null))
        .toList();
  }


  public BookPayload createBook(CreateBookInput input) {
    List<UserError> errors = new ArrayList<>();

    if (input.title() == null || input.title().isBlank()) {
      errors.add(new UserError("Title is required", "title", ErrorCode.INVALID_INPUT));
    }

    Author author = authorRepository.findById(input.authorId()).orElse(null);
    if (author == null) {
      errors.add(new UserError("Author not found", "authorId", ErrorCode.NOT_FOUND));
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

    Book saved = bookRepository.save(book);

    return new BookPayload(toDto(saved), List.of());
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
        .filter(book -> book.publisherId() != null)     // ← add here
        .map(BookDto::publisherId)
        .distinct()
        .toList();

    Map<Long, Publisher> publisherById = publisherRepository.findAllById(publisherIds).stream()
        .collect(Collectors.toMap(Publisher::getId, a -> a));

    return books.stream()
        .filter(book -> book.publisherId() != null)     // ← and here
        .collect(Collectors.toMap(
            book -> book,
            book -> publisherById.get(book.publisherId())
        ));
  }


  private BookDto toDto(Book book) {
    Long publisherID = book.getPublisher() != null ? book.getPublisher().getId() : null;
    return new BookDto(book.getId() , book.getTitle(),book.getPublishedYear() , book.getAuthor().getId() , publisherID);
  }
  private BookPayload  toPayload(Optional<Book> bookOptional) {
    List<UserError> errors = new ArrayList<>();

    if (bookOptional.isEmpty()) {
      errors.add( new UserError("Book not found", "bookId", ErrorCode.NOT_FOUND));
      return new BookPayload(null, errors);
    }
    Book book = bookOptional.get();
    Long publisherID = book.getPublisher() != null ? book.getPublisher().getId() : null;
    BookDto bookDto = new BookDto(book.getId(), book.getTitle(), book.getPublishedYear(),
        book.getAuthor().getId(), publisherID);
    return new BookPayload(bookDto, errors);
  }
}
