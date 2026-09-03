package com.ai_document_knowledge_assistant.model;

/**
 * A chunk selected during retrieval.
 */
public record RetrievedChunk(
        String documentId,
        String chunkId,
        int chunkIndex,
        String content,
        double similarity
) {
}