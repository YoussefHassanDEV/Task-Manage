package com.example.Task.Manage.config;

import com.example.Task.Manage.DTOs.Response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.Instant;

public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private ObjectMapper mapper;

    public RestAuthenticationEntryPoint() { }

    @Autowired
    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse body = new ErrorResponse(
                Instant.now(), 401, "Unauthorized", "Authentication required", request.getRequestURI(), null);
        mapper.writeValue(response.getOutputStream(), body);
    }
}
