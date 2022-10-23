package com.bitbytejoy.neuefischetodo.repository;

import com.bitbytejoy.neuefischetodo.model.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TodoRepository extends MongoRepository<Todo, String> {
    Page<Todo> findAllByUserId(Pageable pageable, String userId);

    Todo findByUserIdAndId(String userId, String id);
}