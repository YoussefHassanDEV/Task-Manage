package com.example.Task.Manage.controller;

import com.example.Task.Manage.DTOs.Request.TaskCreateRequest;
import com.example.Task.Manage.DTOs.Request.TaskRequest;
import com.example.Task.Manage.DTOs.Response.TaskResponse;
import com.example.Task.Manage.Enum.TaskStatus;
import com.example.Task.Manage.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@WithMockUser(username = "example@gmail.com")
class TaskControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockBean TaskService taskService;

    @Test
    void create_returnsTaskResponse() throws Exception {
        TaskCreateRequest req = new TaskCreateRequest("t", "d");
        TaskResponse resp = new TaskResponse(1L, "t", "d", TaskStatus.DONE);
        when(taskService.create(eq("example@gmail.com"), any(TaskRequest.class))).thenReturn(resp);

        mvc.perform(post("/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(req)))
                .andExpect(status().isCreated()).andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(resp.id()))
                .andExpect(jsonPath("$.title").value("t"))
                .andExpect(jsonPath("$.description").value("d"))
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    void list_returnsOk() throws Exception {
        mvc.perform(get("/tasks"))
                .andExpect(status().isOk());
    }
}
