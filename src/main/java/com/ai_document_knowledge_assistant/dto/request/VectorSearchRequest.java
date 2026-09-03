package com.ai_document_knowledge_assistant.dto.request;

import java.util.List;

/**
 * Vector search request.
 */
public record VectorSearchRequest(
       List<String> documentIds,
        String query,
        int topK
) {
}