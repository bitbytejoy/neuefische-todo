package com.bitbytejoy.neuefischetodo.controller;

import com.bitbytejoy.neuefischetodo.model.Todo;
import com.bitbytejoy.neuefischetodo.repository.TodoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/todos")
public class TodoController {
    private final TodoRepository todoRepository;

    public TodoController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @PostMapping
    public Todo post(@Valid @RequestBody Todo todo) {
        String userId = (String)SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

        todo.setUserId(userId);

        todoRepository.save(todo);

        return todo;
    }

    @PutMapping("{id}")
    public Todo put(
        @PathVariable("id") String id,
        @Valid @RequestBody Todo todo
    ) {
        String userId = (String)SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

        todo.setId(id);
        todo.setUserId(userId);

        // Create if not exists
        if (!todoRepository.existsById(id)) {
            return this.post(todo);
        }

        // Check if owner
        Todo oldTodo = todoRepository.findByUserIdAndId(userId, id);
        if (!oldTodo.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        // Replace
        todoRepository.save(todo);

        return todo;
    }

    @GetMapping
    public Page<Todo> get(Pageable pageable) {
        String userId = (String)SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return todoRepository.findAllByUserId(pageable, userId);
    }
}
