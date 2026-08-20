package com.ai_document_knowledge_assistant.model;


public record RetrievedChunk(
        String documentId,
        String chunkId,
        int chunkIndex,
        String content,
        double similarity
) {
}