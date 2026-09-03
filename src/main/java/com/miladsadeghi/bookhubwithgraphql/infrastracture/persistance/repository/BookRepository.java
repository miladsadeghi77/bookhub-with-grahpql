package com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.repository;

import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

}
