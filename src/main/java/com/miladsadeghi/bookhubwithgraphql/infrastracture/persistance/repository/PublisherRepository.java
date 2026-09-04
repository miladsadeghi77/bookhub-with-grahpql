package com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.repository;

import com.miladsadeghi.bookhubwithgraphql.infrastracture.persistance.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {

}
