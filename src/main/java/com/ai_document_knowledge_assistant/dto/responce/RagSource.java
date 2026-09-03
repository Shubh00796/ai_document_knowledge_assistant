package com.ai_document_knowledge_assistant.dto.responce;

/**
 * A source used in a RAG answer.
 */
public record RagSource(
        String documentId,
        int chunkIndex,
        double similarity
) {
}