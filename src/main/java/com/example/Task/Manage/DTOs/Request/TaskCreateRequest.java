package com.example.Task.Manage.DTOs.Request;

public class TaskCreateRequest {
    private String title;
    private String description;

    // Default constructor for JSON deserialization
    public TaskCreateRequest() {
    }

    // Constructor with parameters
    public TaskCreateRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
