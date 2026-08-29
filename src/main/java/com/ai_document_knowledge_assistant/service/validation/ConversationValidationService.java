package com.ai_document_knowledge_assistant.service.validation;

import com.ai_document_knowledge_assistant.model.entity.ConversationEntity;

/**
 * Service responsible for validating conversation-related operations and business rules.
 *
 * This service ensures data integrity and business rule compliance for conversation
 * management operations.
 *
 * @author AI Document Knowledge Assistant
 * @version 1.0
 */
public interface ConversationValidationService {

    /**
     * Validates that the provided conversation ID is not null or blank.
     *
     * @param conversationId the conversation ID to validate
     * @throws IllegalArgumentException if the conversation ID is null or blank
     */
    void validateConversationIdNotBlank(String conversationId);

    /**
     * Validates that the provided message content is not null or blank.
     *
     * @param content the message content to validate
     * @throws IllegalArgumentException if the content is null or blank
     */
    void validateMessageContentNotBlank(String content);

    /**
     * Retrieves a conversation by ID or throws an exception if not found.
     *
     * @param conversationId the conversation ID to retrieve
     * @return the conversation entity
     * @throws com.ai_document_knowledge_assistant.exception.ResourceNotFoundException
     *         if the conversation is not found
     */
    ConversationEntity getConversationByIdOrThrow(String conversationId);
}

