package com.ai_document_knowledge_assistant.exception;


/**
 * Thrown when a requested resource cannot be found.
 *
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Creates a new exception with the specified message.
     *
     * @param message exception message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
