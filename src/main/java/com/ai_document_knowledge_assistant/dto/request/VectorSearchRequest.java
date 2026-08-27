package com.ai_document_knowledge_assistant.dto.request;

import java.util.List;

public record VectorSearchRequest(
       List< String> documentIds,
        String query,
        int topK
) {
}