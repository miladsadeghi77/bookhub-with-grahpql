package com.miladsadeghi.bookhubwithgraphql.api.payload;

import com.miladsadeghi.bookhubwithgraphql.api.dto.BookDto;
import com.miladsadeghi.bookhubwithgraphql.api.error.UserError;
import java.util.List;

public class BookPayload {
  BookDto book;
  List<UserError> errors;

  public BookPayload(BookDto book, List<UserError> errors) {
    this.book = book;
    this.errors = errors;
  }
}
