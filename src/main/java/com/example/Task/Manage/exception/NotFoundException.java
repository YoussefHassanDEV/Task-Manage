// src/main/java/com/example/todo/exception/NotFoundException.java
package com.example.Task.Manage.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
