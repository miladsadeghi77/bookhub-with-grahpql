package com.miladsadeghi.bookhubwithgraphql.api.dto;

public record BookDto(Long id, String title, Integer publishedYear,
                      Long authorId, Long publisherId) {}