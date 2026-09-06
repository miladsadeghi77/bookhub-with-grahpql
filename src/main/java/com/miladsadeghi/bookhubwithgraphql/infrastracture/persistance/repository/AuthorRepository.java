package com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.repository;

import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {

}
