package com.example.bankcards.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Object> handleInsufficientFundsException(
            InsufficientFundsException ex, WebRequest request) {
        return compileResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedRuntimeException.class)
    public ResponseEntity<Object> handleAccessDeniedRuntimeException(
            AccessDeniedRuntimeException ex, WebRequest request) {
        return compileResponse(ex, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Object> handleInvalidRequestException(
            InvalidRequestException ex, WebRequest request) {
        return compileResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(
            NotFoundException ex, WebRequest request) {
        return compileResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedTransferException.class)
    public ResponseEntity<Object> handleUnauthorizedTransferException(
            UnauthorizedTransferException ex, WebRequest request) {
        return compileResponse(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        return compileResponse(ex, HttpStatus.I_AM_A_TEAPOT);
    }

    private ResponseEntity<Object> compileResponse(Exception ex, HttpStatus status){
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, status);
    }
}