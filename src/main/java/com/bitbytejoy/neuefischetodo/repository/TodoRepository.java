package com.bitbytejoy.neuefischetodo.repository;

import com.bitbytejoy.neuefischetodo.model.Todo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TodoRepository extends MongoRepository<Todo, String> {
}