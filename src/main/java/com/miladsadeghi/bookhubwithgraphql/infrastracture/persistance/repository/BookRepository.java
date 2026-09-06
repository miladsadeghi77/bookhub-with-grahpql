package com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.repository;

import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.entity.Book;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

  boolean existsByTitleAndAuthorIdAndPublisherId(@NotBlank(message = "Title is required") String title, @NotNull(message = "Author is required") Long authorId, Long publisherId);
}
