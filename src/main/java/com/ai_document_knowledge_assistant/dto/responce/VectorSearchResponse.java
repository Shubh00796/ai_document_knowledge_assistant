package com.ai_document_knowledge_assistant.dto.responce;


public record VectorSearchResponse(
        String documentId,
        int chunkIndex,
        String content,
        double similarity
) {
}