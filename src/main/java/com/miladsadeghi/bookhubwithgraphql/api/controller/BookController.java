package com.miladsadeghi.bookhubwithgraphql.api.controller;
import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.entity.Book;
import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.repository.BookRepository;
import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class BookController   {
/*  private final List<Book> books = List.of(
      new Book("1" , "Pragmatic Programmer" , 2002),
      new Book("2" , "Spring in Action" , 2014),
      new Book("3" , "effective java" , 2008)
  );
*/
  private final BookRepository bookRepository;

  public BookController(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  @QueryMapping
  public Book book(@Argument(name = "id") String id) {
    return bookRepository.findById(Long.parseLong(id)).orElseThrow(()-> new RuntimeException("Book not found"));
  }

  @QueryMapping
  public List<Book> books() {
    return bookRepository.findAll();
  }

}
