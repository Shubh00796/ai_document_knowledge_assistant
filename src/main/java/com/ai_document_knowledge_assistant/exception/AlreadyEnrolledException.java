package com.ai_document_knowledge_assistant.exception;

/**
 * Thrown when a student is already enrolled in a selected course.
 *
 */
public class AlreadyEnrolledException extends RuntimeException {

    /**
     * Creates a new exception with the specified message.
     *
     * @param message exception message
     */
    public AlreadyEnrolledException(String message) {
        super(message);
    }
}

