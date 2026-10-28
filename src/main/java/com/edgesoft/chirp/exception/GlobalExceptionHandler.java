package com.edgesoft.chirp.exception;

import com.edgesoft.chirp.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public  ResponseEntity<ErrorResponse> dataIntegrityHandler(Exception exception, HttpServletRequest request){
        HttpStatus status = HttpStatus.CONFLICT;
        log.error("An exception occured in server " + exception);
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message("User already registered " + exception.getClass())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> allExceptionHandler(Exception exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_GATEWAY;
        log.error("An exception occured in server " + exception);
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message("An unexpected error occurred. Please try again later. " + exception.getClass())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, status);
    }

}
