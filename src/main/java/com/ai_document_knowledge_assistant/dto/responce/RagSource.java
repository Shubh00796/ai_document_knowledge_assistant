package com.ai_document_knowledge_assistant.dto.responce;

public record RagSource(
        String documentId,
        int chunkIndex,
        double similarity
) {
}