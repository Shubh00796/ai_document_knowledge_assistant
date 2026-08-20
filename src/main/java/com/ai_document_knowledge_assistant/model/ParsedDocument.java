package com.ai_document_knowledge_assistant.model;

public record ParsedDocument(
        String fileName,
        String contentType,
        String text,
        int pageCount
) {
}