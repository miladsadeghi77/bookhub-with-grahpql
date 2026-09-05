package com.miladsadeghi.bookhubwithgraphql.api.controller;

import com.miladsadeghi.bookhubwithgraphql.api.dto.BookDto;
import com.miladsadeghi.bookhubwithgraphql.api.input.CreateBookInput;
import com.miladsadeghi.bookhubwithgraphql.api.payload.BookPayload;
import com.miladsadeghi.bookhubwithgraphql.domain.model.usecase.BookUseCase;
import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.entity.Author;
import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.entity.Publisher;
import java.util.List;
import java.util.Map;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class BookController {

  private final BookUseCase bookUseCase;

  public BookController(BookUseCase bookUseCase) {
    this.bookUseCase = bookUseCase;
  }

  @QueryMapping
  public BookPayload book(@Argument(name = "id") String id) {
    return bookUseCase.book(id);
  }

  @QueryMapping
  public List<BookDto> books() {
    return bookUseCase.books();
  }

  @MutationMapping
  public BookPayload createBook(@Argument CreateBookInput input) {

    return bookUseCase.createBook(input);
  }


  @BatchMapping(typeName = "Book", field = "author")
  public Map<BookDto, Author> author(List<BookDto> books) {
    return bookUseCase.author(books);
  }

  @BatchMapping(typeName = "Book", field = "publisher")
  public Map<BookDto, Publisher> publisher(List<BookDto> books) {
    return bookUseCase.publisher(books);
  }
}
