package com.ai_document_knowledge_assistant.dto.responce;


import java.time.LocalDateTime;

/**
 * Conversation details.
 */
public record ConversationResponse(
        String id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}