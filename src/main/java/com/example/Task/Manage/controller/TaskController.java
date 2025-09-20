package com.example.Task.Manage.controller;

import com.example.Task.Manage.DTOs.Request.TaskRequest;
import com.example.Task.Manage.DTOs.Request.UpdateTaskStatusRequest;
import com.example.Task.Manage.DTOs.Response.TaskResponse;
import com.example.Task.Manage.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<TaskResponse> create(Authentication auth, @RequestBody @Valid TaskRequest req) {
        TaskResponse resp = taskService.create(auth.getName(), req);
        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(resp);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> list(Authentication auth) {
        return ResponseEntity.ok(taskService.list(auth.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateStatus(Authentication auth,
                                                     @PathVariable("id") Long id,
                                                     @RequestBody @Valid UpdateTaskStatusRequest req) {
        return ResponseEntity.ok(taskService.updateStatus(auth.getName(), id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable("id") Long id) {
        taskService.delete(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
