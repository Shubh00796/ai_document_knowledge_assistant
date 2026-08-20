package com.ai_document_knowledge_assistant.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Handles application exceptions and converts them into consistent API errors.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles resource-not-found exceptions.
     *
     * @param ex exception thrown when a resource cannot be found
     * @param request current HTTP request
     * @return a 404 error response
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        ApiError error = getApiError(HttpStatus.NOT_FOUND, ex.getMessage(), request);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(error);
    }


    /**
     * Handles duplicate-resource exceptions.
     *
     * @param ex exception thrown when a duplicate resource is detected
     * @param request current HTTP request
     * @return a 409 error response
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicateResourceException(
            DuplicateResourceException ex,
            HttpServletRequest request) {

        ApiError error = getApiError(HttpStatus.CONFLICT, ex.getMessage(), request);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(error);
    }

    /**
     * Handles validation failures raised by Spring MVC.
     *
     * @param ex validation exception
     * @param request current HTTP request
     * @return a 400 error response containing validation messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ApiError error = getApiError(HttpStatus.BAD_REQUEST, message, request);

        return ResponseEntity.badRequest()
                .body(error);
    }

    /**
     * Handles unexpected exceptions.
     *
     * @param ex unexpected exception
     * @param request current HTTP request
     * @return a 500 error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {

        ApiError error = getApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }


    /**
     * Builds a standardized API error response.
     *
     * @param status HTTP status to use
     * @param message error message
     * @param request current HTTP request
     * @return standardized API error payload
     */
    private static ApiError getApiError(HttpStatus status, String message, HttpServletRequest request) {
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();
        return error;
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        ApiError error =
                getApiError(
                        HttpStatus.BAD_REQUEST,
                        ex.getMessage(),
                        request
                );

        return ResponseEntity.badRequest()
                .body(error);
    }
}