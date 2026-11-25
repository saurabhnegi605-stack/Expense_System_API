package com.expensemanagement.expense_api.exception;

import com.expensemanagement.expense_api.DTO.ErrorResponse ;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException e, HttpServletRequest request) {

        return buildResponse(HttpStatus.NOT_FOUND,e.getMessage(),request.getRequestURI(),Map.of()) ;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>() ;
                e.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(),error.getDefaultMessage()));
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation Failed",request.getRequestURI(),errors) ;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestBody
            (HttpMessageNotReadableException e,HttpServletRequest request){
        return buildResponse(HttpStatus.BAD_REQUEST,"Invalid request body or date formate",request.getRequestURI(),Map.of());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation
            (ConstraintViolationException e,HttpServletRequest request){
        return buildResponse(HttpStatus.BAD_REQUEST,"Constraint validation failed",request.getRequestURI(),Map.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,String>> handleBadRequest(IllegalArgumentException e){
        return ResponseEntity.badRequest().body(Map.of("message",e.getMessage())) ;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception e, HttpServletRequest request) {

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,"An unexpected error has occurred",request.getRequestURI(),Map.of());
    }


    // JSON error response
    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            String path,
            Map<String,String> validationErrors
    ){
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                validationErrors) ;

        return ResponseEntity.status(status).body(response) ;
    }
}
