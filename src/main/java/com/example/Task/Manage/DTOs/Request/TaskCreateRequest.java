package com.example.Task.Manage.DTOs.Request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskCreateRequest {
    private String title;
    private String description;

    public TaskCreateRequest() {
    }

    public TaskCreateRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }

}
