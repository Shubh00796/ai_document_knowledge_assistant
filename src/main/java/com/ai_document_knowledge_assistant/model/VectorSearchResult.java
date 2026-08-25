package com.ai_document_knowledge_assistant.model;


public record VectorSearchResult(
        String documentId,
        VectorDocument document,
        double similarity
) {
}