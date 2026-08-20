package com.ai_document_knowledge_assistant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.embedding.ollama")
public record EmbeddingProperties(
        String baseUrl,
        String model
) {
}