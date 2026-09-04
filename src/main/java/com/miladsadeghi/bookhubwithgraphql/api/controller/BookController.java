package com.miladsadeghi.bookhubwithgraphql.api.controller;

import com.miladsadeghi.bookhubwithgraphql.api.dto.BookDto;
import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.entity.Author;
import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.entity.Book;
import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.entity.Publisher;
import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.repository.AuthorRepository;
import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.repository.BookRepository;
import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.repository.PublisherRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class BookController {
  private final BookRepository bookRepository;
  private final AuthorRepository authorRepository;
  private final PublisherRepository publisherRepository;

  public BookController(BookRepository bookRepository, AuthorRepository authorRepository,
      PublisherRepository publisherRepository) {
    this.bookRepository = bookRepository;
    this.authorRepository = authorRepository;
    this.publisherRepository = publisherRepository;
  }

  @QueryMapping
  public BookDto book(@Argument(name = "id") String id) {
    Book book = bookRepository.findById(Long.parseLong(id))
        .orElseThrow(() -> new RuntimeException("Book not found"));
    Long publisherID = book.getPublisher() != null ? book.getPublisher().getId() : null;
    return new BookDto(book.getId() , book.getTitle(),book.getPublishedYear() , book.getAuthor().getId() , publisherID);
  }

  @QueryMapping
  public List<BookDto> books() {
    return bookRepository.findAll().stream()
        .map(book -> new BookDto(book.getId(), book.getTitle(),
            book.getPublishedYear(),
            book.getAuthor().getId(),
            book.getPublisher() != null ? book.getPublisher().getId() : null))
        .toList();
  }

  @BatchMapping(typeName = "Book", field = "author")
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

  @BatchMapping(typeName = "Book", field = "publisher")
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
}
