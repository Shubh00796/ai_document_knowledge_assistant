package com.ai_document_knowledge_assistant.model;


public record VectorSearchResult(
        VectorDocument document,
        double similarity
) {
}