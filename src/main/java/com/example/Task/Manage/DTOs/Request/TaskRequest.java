package com.example.Task.Manage.DTOs.Request;

import com.example.Task.Manage.Enum.TaskStatus;
import jakarta.validation.constraints.NotBlank;

public record TaskRequest(
        @NotBlank String title,
        String description,
        TaskStatus status
) {}
