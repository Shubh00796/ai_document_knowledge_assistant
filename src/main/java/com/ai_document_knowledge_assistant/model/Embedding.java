package com.ai_document_knowledge_assistant.model;


import java.util.List;

public record Embedding(
        List<Float> vector
) {
}