package com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.repository;

import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.entity.Book;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

  boolean existsByTitleAndAuthorIdAndPublisherId(@NotBlank(message = "Title is required") String title, @NotNull(message = "Author is required") Long authorId, Long publisherId);
}
