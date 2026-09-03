package com.ai_document_knowledge_assistant.exception;

/**
 * Thrown when vector store operations fail.
 */
public class VectorStoreException extends RuntimeException {
    public VectorStoreException(String message) {
        super(message);
    }

    public VectorStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
