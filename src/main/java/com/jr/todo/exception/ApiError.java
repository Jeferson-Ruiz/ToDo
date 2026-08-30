package com.jr.todo.exception;

import java.io.IOException;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public record ApiError(int status, String message, String path) {

  public static void write(HttpServletRequest request, HttpServletResponse response, ObjectMapper objectMapper,
      int status, String message) throws IOException {
    response.setStatus(status);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    ApiError apiError = new ApiError(status, message, request.getRequestURI());
    objectMapper.writeValue(response.getOutputStream(), apiError);
  }
}
