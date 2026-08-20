package com.ai_document_knowledge_assistant.model;

import java.util.List;

public record VectorDocument(
        String id,
        String documentId,
        int chunkIndex,
        String content,
        List<Float> embedding
) {
}
