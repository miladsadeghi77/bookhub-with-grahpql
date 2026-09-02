package com.miladsadeghi.bookhubwithgraphql.api.controller;
import com.miladsadeghi.bookhubwithgraphql.api.dto.BookDTO;
import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class BookController   {
  private final List<BookDTO> books = List.of(
      new BookDTO("1" , "Pragmatic Programmer" , 2002),
      new BookDTO("2" , "Spring in Action" , 2014),
      new BookDTO("3" , "effective java" , 2008)
  );

  @QueryMapping
  public BookDTO book(@Argument(name = "id") String id) {
    return books.stream()
        .filter( book -> book.id().equals(id) )
        .findFirst()
        .orElse(new BookDTO("10" , "Example" , 2012));
  }

  @QueryMapping
  public List<BookDTO> books() {
    return books;
  }
}
