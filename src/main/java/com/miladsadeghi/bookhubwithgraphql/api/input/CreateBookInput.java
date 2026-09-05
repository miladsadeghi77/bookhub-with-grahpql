package com.miladsadeghi.bookhubwithgraphql.api.input;

public record CreateBookInput(String title, Long authorId, Long publisherId,
                              Integer publishedYear) {

}
