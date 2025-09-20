package com.example.Task.Manage.DTOs.Request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskCreateRequest {
    // Getters and setters
    private String title;
    private String description;

    public TaskCreateRequest() {
    }

    public TaskCreateRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }

}
