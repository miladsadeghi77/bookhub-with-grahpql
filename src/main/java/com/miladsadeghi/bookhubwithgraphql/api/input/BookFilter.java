package com.miladsadeghi.bookhubwithgraphql.api.input;

public record BookFilter( String titleContains,
                          Long authorId,
                          Long publisherId,
                          Integer minPublishedYear,
                          Integer maxPublishedYear) {

}
