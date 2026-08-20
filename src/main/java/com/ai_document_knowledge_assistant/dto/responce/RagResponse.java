package com.ai_document_knowledge_assistant.dto.responce;

import java.util.List;

public record RagResponse(
        String answer,
        List<RagSource> sources
) {
}