package com.ai_document_knowledge_assistant.model;


public record DocumentChunk(
        int chunkIndex,
        String content,
        int startOffset,
        int endOffset,
        int pageNumber

) {
}