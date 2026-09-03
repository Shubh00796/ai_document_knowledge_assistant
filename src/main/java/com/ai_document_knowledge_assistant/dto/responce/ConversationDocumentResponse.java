package com.ai_document_knowledge_assistant.dto.responce;


public record ConversationDocumentResponse(
        Long id,
        String conversationId,
        String documentId
) {
}