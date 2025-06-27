package org.example.expert.domain.todo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController// Tells Spring that this class will handle web requests and return data (usually JSON), not HTML pages. Combines @Controller and @ResponseBody.
@RequiredArgsConstructor // From Lombok. It generates a constructor for all final fields.
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/todos")
    public ResponseEntity<TodoSaveResponse> saveTodo(
            @Auth AuthUser authUser, // Custom annotation! Injects the currently authenticated user into the method.
            @Valid @RequestBody TodoSaveRequest todoSaveRequest // @Valid : Tells Spring to validate todoSaveRequest. Throws an error if the request is invalid.
    ) {
        return ResponseEntity.ok(todoService.saveTodo(authUser, todoSaveRequest));
    }

    @GetMapping("/todos") // Endpoint: Get List of Todos (with Pagination)
    public ResponseEntity<Page<TodoResponse>> getTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
            // @RequestParam : Binds query parameters (like ?page=2&size=5) to the method arguments.
            // defaultValue : gives a value if the parameter isn’t provided in the URL.
            // Here, defaults to page 1, size 10.
    ) {
        return ResponseEntity.ok(todoService.getTodos(page, size));
    }

    @GetMapping("/todos/{todoId}")
    public ResponseEntity<TodoResponse> getTodo(@PathVariable long todoId) {
        return ResponseEntity.ok(todoService.getTodo(todoId));
    }
}


// Key part of the error: Connection is read-only. Queries leading to data modification are not allowed
// Your application is trying to execute an INSERT statement (adding a new row to the todos table).
// The database connection used for this operation is set to read-only.
// Since it's read-only, any operations that modify data (like INSERT, UPDATE, or DELETE) are not allowed
// Only queries that read data (SELECT) will work.
// Where to look :
// 1. Transaction or DataSource Settings. : @Transactional(readOnly = true) (service layer)
// 2. DataSource Configuration : spring.datasource.hikari.read-only=true (application.properties)
// 3. Database User Permissions : the DB user that your Spring Boot app connects as does not have permission to write to the database.
