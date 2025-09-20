package com.example.Task.Manage.repository;


import com.example.Task.Manage.model.Task;
import com.example.Task.Manage.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired TaskRepository tasks;
    @Autowired
    TestEntityManager em;

    @Test
    void findByOwner_returnsTasks() {
        var user = new User();
        user.setEmail("u@example.com");
        user.setName("Test User");
        user.setPasswordHash("x");
        em.persist(user);

        var t1 = new Task();
        t1.setTitle("A"); t1.setOwner(user);
        var t2 = new Task();
        t2.setTitle("B"); t2.setOwner(user);
        em.persist(t1); em.persist(t2);
        em.flush();

        List<Task> list = tasks.findByOwner(user);

        assertEquals(2, list.size());
    }
}
