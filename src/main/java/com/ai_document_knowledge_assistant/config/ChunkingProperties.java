package com.ai_document_knowledge_assistant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Chunking configuration.
 */
@ConfigurationProperties(prefix = "app.chunking")
public record ChunkingProperties(
        int chunkSize,
        int overlap
) {
}