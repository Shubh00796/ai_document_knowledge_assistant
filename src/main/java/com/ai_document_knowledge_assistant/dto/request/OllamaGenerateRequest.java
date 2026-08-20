package com.ai_document_knowledge_assistant.dto.request;

public record OllamaGenerateRequest(
        String model,
        String prompt,
        boolean stream
) {
}
