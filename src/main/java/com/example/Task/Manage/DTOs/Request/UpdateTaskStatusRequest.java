package com.example.Task.Manage.DTOs.Request;

import com.example.Task.Manage.Enum.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequest(@NotNull TaskStatus status) {}
