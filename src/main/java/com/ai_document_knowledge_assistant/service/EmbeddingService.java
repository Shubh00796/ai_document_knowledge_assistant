package com.ai_document_knowledge_assistant.service;


import com.ai_document_knowledge_assistant.model.Embedding;

public interface EmbeddingService {

    Embedding embed(String text);
}