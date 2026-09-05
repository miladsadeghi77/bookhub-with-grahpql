package com.miladsadeghi.bookhubwithgraphql.api.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateBookInput(
    @NotBlank(message = "Title is required") String title,
    @NotNull(message = "Author is required") Long authorId,
    Long publisherId,
    @Positive(message = "Published year must be positive") Integer publishedYear
) {

}
