package com.example.Task.Manage.DTOs.Response;


import com.example.Task.Manage.Enum.TaskStatus;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status
) {}
