package com.ai_document_knowledge_assistant.exception;


/**
 * Thrown when a resource already exists and violates uniqueness rules.
 *
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * Creates a new exception with the specified message.
     *
     * @param message exception message
     */
    public DuplicateResourceException(String message) {
        super(message);
    }
}

