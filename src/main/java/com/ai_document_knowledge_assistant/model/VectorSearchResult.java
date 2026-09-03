package com.ai_document_knowledge_assistant.model;

/**
 * A vector search match.
 */
public record VectorSearchResult(
        String documentId,
        VectorDocument document,
        double similarity
) {
}