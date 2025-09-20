package com.example.Task.Manage.config;

import com.example.Task.Manage.DTOs.Response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.Instant;

public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private ObjectMapper mapper;

    public RestAccessDeniedHandler() { }

    @Autowired
    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse body = new ErrorResponse(
                Instant.now(), 403, "Forbidden", ex.getMessage(), request.getRequestURI(), null);
        mapper.writeValue(response.getOutputStream(), body);
    }
}
