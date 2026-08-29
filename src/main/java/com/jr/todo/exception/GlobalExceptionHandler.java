package com.jr.todo.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleIllegalArgument(IllegalArgumentException ex) {
    return Map.of("Error", ex.getMessage());
  }

  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, String> handleEntityNotFound(EntityNotFoundException ex) {
    return Map.of("Error", ex.getMessage());
  }

  @ExceptionHandler(IllegalStateException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleIllegalState(IllegalStateException ex) {
    return Map.of("Error", ex.getMessage());
  }

  // SPA JSON: auth failures → 401/403 JSON en vez de HTML/whitelabel
  @ExceptionHandler(BadCredentialsException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public Map<String, String> handleBadCredentials(BadCredentialsException ex) {
    return Map.of("Error", ex.getMessage());
  }

  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public Map<String, String> handleAuthentication(AuthenticationException ex) {
    return Map.of("Error", ex.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public Map<String, String> handleAccessDenied(AccessDeniedException ex) {
    return Map.of("Error", "Acceso denegado: " + ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleValidation(MethodArgumentNotValidException ex) {
    return Map.of("Error", ex.getMessage());
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleNotReadable(HttpMessageNotReadableException ex) {
    return Map.of("Error", ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    return Map.of("Error", ex.getMessage());
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleMissingParam(MissingServletRequestParameterException ex) {
    return Map.of("Error", ex.getMessage());
  }

  // Mantener compatibilidad con código que espera IllegalAccessError (dead handler legacy)
  @ExceptionHandler(IllegalAccessError.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> illegalAccessError(IllegalAccessError ex) {
    return Map.of("Error", ex.getMessage());
  }

}
