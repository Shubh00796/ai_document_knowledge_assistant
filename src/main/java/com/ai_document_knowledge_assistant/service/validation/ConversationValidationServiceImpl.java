package com.ai_document_knowledge_assistant.service.validation;

import com.ai_document_knowledge_assistant.model.entity.ConversationEntity;
import com.ai_document_knowledge_assistant.reposiotry_ai.ConversationRepoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Default implementation of ConversationValidationService.
 *
 * This service provides validation logic for conversation-related operations,
 * ensuring data integrity and compliance with business rules.
 *
 * @author AI Document Knowledge Assistant
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ConversationValidationServiceImpl implements ConversationValidationService {

    private final ConversationRepoService conversationRepoService;

    private static final String CONVERSATION_ID_BLANK_ERROR_MESSAGE =
            "Conversation ID cannot be blank";
    private static final String MESSAGE_CONTENT_BLANK_ERROR_MESSAGE =
            "Message content cannot be blank";

    @Override
    public void validateConversationIdNotBlank(final String conversationId) {
        if (conversationId == null || conversationId.isBlank()) {
            throw new IllegalArgumentException(CONVERSATION_ID_BLANK_ERROR_MESSAGE);
        }
    }

    @Override
    public void validateMessageContentNotBlank(final String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException(MESSAGE_CONTENT_BLANK_ERROR_MESSAGE);
        }
    }

    @Override
    public ConversationEntity getConversationByIdOrThrow(final String conversationId) {
        // ConversationRepoService.findByConversationId already throws ResourceNotFoundException
        return conversationRepoService.findByConversationId(conversationId);
    }
}

