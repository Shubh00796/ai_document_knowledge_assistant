package com.ai_document_knowledge_assistant.service;


import com.ai_document_knowledge_assistant.model.Embedding;

/**
 * Creates embeddings for text.
 */
public interface EmbeddingService {

    /** Embeds the given text. */
    Embedding embed(String text);
}