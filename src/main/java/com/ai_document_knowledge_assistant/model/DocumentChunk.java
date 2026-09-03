package com.ai_document_knowledge_assistant.model;

/**
 * A chunk extracted from a document.
 */
public record DocumentChunk(
        int chunkIndex,
        String content,
        int startOffset,
        int endOffset,
        int pageNumber

) {
}