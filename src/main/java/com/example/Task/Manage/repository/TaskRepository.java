// src/main/java/com/example/Task/Manage/repository/TaskRepository.java
package com.example.Task.Manage.repository;

import com.example.Task.Manage.model.Task;
import com.example.Task.Manage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByOwner(User owner);
    Optional<Task> findByIdAndOwner(Long id, User owner);
}
