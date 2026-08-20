package com.ai_document_knowledge_assistant.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Standard API error payload returned by exception handlers.
 */
@Getter
@Builder
@AllArgsConstructor
public class ApiError {

    /** Timestamp when the error occurred. */
    private LocalDateTime timestamp;

    /** HTTP status code associated with the error. */
    private int status;

    /** HTTP reason phrase associated with the error. */
    private String error;

    /** Human-readable error message. */
    private String message;

    /** Request path that triggered the error. */
    private String path;
}