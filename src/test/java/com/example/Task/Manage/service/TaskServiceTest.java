package com.example.Task.Manage.service;


import com.example.Task.Manage.DTOs.Response.TaskResponse;
import com.example.Task.Manage.Enum.TaskStatus;
import com.example.Task.Manage.model.Task;
import com.example.Task.Manage.DTOs.Request.TaskRequest;
import com.example.Task.Manage.model.User;
import com.example.Task.Manage.repository.TaskRepository;
import com.example.Task.Manage.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    TaskRepository tasks;
    
    @Mock
    UserRepository userRepository;
    @InjectMocks TaskService service;

    @Test
    void create_returnsResponse() {
        // Arrange
        String userEmail = "test@example.com";
        var owner = User.builder().id(1L).email(userEmail).passwordHash("p").build();
        var saved = Task.builder()
                .id(42L).title("t").description("d").status(TaskStatus.OPEN).owner(owner)
                .build();
        
        when(userRepository.findByEmail(userEmail)).thenReturn(java.util.Optional.of(owner));
        when(tasks.save(any(Task.class))).thenReturn(saved);

        // Act
        TaskRequest request = new TaskRequest("t", "d", null);
        TaskResponse resp = service.create(userEmail, request);

        // Assert
        assertEquals(42L, resp.id());
        assertEquals("t", resp.title());
        assertEquals(TaskStatus.OPEN, resp.status());
    }
}
