package com.ai_document_knowledge_assistant.dto.request;

public record VectorSearchRequest(
        String documentId,
        String query,
        int topK
) {
}