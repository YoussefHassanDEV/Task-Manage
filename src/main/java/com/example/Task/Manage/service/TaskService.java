package com.example.Task.Manage.service;

import com.example.Task.Manage.DTOs.Request.TaskRequest;
import com.example.Task.Manage.DTOs.Request.UpdateTaskStatusRequest;
import com.example.Task.Manage.DTOs.Response.TaskResponse;
import com.example.Task.Manage.Enum.TaskStatus;
import com.example.Task.Manage.exception.NotFoundException;
import com.example.Task.Manage.model.Task;
import com.example.Task.Manage.model.User;
import com.example.Task.Manage.repository.TaskRepository;
import com.example.Task.Manage.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    private User requireUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public TaskResponse create(String email, TaskRequest req) {
        User owner = requireUser(email);
        Task t = Task.builder()
                .title(req.title())
                .description(req.description())
                .status(req.status() == null ? TaskStatus.OPEN : req.status())
                .owner(owner)
                .build();
        Task saved = taskRepository.save(t);
        return toDto(saved);
    }

    public List<TaskResponse> list(String email) {
        User owner = requireUser(email);
        return taskRepository.findByOwner(owner).stream().map(this::toDto).toList();
    }
    public TaskResponse updateStatus(String email, Long id, UpdateTaskStatusRequest req) {
        User owner = requireUser(email);
        Task t = taskRepository.findById(id).orElseThrow(() -> new NotFoundException("Task not found"));
        if (!t.getOwner().getId().equals(owner.getId())) throw new AccessDeniedException("Forbidden");
        t.setStatus(req.status());
        return toDto(taskRepository.save(t));
    }

    public void delete(String email, Long id) {
        User owner = requireUser(email);
        Task t = taskRepository.findById(id).orElseThrow(() -> new NotFoundException("Task not found"));
        if (!t.getOwner().getId().equals(owner.getId())) throw new AccessDeniedException("Forbidden");
        taskRepository.delete(t);
    }

    private TaskResponse toDto(Task t) {
        return new TaskResponse(t.getId(), t.getTitle(), t.getDescription(), t.getStatus());
    }
}
