package com.ai_document_knowledge_assistant.dto.request;

/**
 * Ollama generation request.
 */
public record OllamaGenerateRequest(
        String model,
        String prompt,
        boolean stream
) {
}
