package com.ai_document_knowledge_assistant.dto.responce;



import com.ai_document_knowledge_assistant.entity.enums.MessageRole;

import java.time.LocalDateTime;

/**
 * Conversation message details.
 */
public record ConversationMessageResponse(
        String id,
        String conversationId,
        MessageRole role,
        String content,
        LocalDateTime createdAt
) {
}