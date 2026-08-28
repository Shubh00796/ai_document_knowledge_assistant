package com.ai_document_knowledge_assistant.service.serviceImpl;

import com.ai_document_knowledge_assistant.dto.request.MessageRequest;
import com.ai_document_knowledge_assistant.dto.responce.ConversationMessageResponse;
import com.ai_document_knowledge_assistant.dto.responce.ConversationResponse;
import com.ai_document_knowledge_assistant.entity.enums.MessageRole;
import com.ai_document_knowledge_assistant.mapper.ConversationMapper;
import com.ai_document_knowledge_assistant.model.entity.ConversationEntity;
import com.ai_document_knowledge_assistant.model.entity.ConversationMessageEntity;
import com.ai_document_knowledge_assistant.reposiotry_ai.ConversationRepoService;
import com.ai_document_knowledge_assistant.service.ConversationService;
import com.ai_document_knowledge_assistant.service.validation.ConversationValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Service implementation for managing conversations and messages.
 *
 * This service provides functionality for creating conversations, adding messages
 * from both users and assistants, and retrieving message history. It follows the
 * separation of concerns principle by delegating validation to a dedicated validation
 * service and data persistence to repository services.
 *
 * @author AI Document Knowledge Assistant
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepoService conversationRepoService;
    private final ConversationMapper conversationMapper;
    private final ConversationValidationService conversationValidationService;
    private final ObjectProvider<Supplier<ConversationEntity>> conversationEntityFactoryProvider;
    private final ObjectProvider<Supplier<ConversationMessageEntity>> messageEntityFactoryProvider;

    /**
     * Creates a new conversation.
     *
     * @return a response object containing the created conversation details
     */
    @Override
    public ConversationResponse createConversation() {
        ConversationEntity newConversation = conversationEntityFactoryProvider
                .getIfAvailable(() -> ConversationEntity::new)
                .get();
        ConversationEntity savedConversation =
                conversationRepoService.saveConversation(newConversation);
        return conversationMapper.toResponse(savedConversation);
    }

    /**
     * Adds a user message to an existing conversation.
     *
     * @param conversationId the ID of the conversation
     * @param request the message request containing the user's message content
     * @return a response object containing the saved message details
     * @throws IllegalArgumentException if conversationId is blank
     * @throws com.ai_document_knowledge_assistant.exception.ResourceNotFoundException
     *         if the conversation does not exist
     */
    @Override
    public ConversationMessageResponse addUserMessage(
            final String conversationId,
            final MessageRequest request
    ) {
        Objects.requireNonNull(request, "Message request cannot be null");
        return createAndPersistMessage(
                conversationId,
                request.content(),
                MessageRole.USER
        );
    }

    /**
     * Adds an assistant message to an existing conversation.
     *
     * @param conversationId the ID of the conversation
     * @param request the message request containing the assistant's message content
     * @return a response object containing the saved message details
     * @throws IllegalArgumentException if conversationId is blank
     * @throws com.ai_document_knowledge_assistant.exception.ResourceNotFoundException
     *         if the conversation does not exist
     */
    @Override
    public ConversationMessageResponse addAssistantMessage(
            final String conversationId,
            final MessageRequest request
    ) {
        Objects.requireNonNull(request, "Message request cannot be null");
        return createAndPersistMessage(
                conversationId,
                request.content(),
                MessageRole.ASSISTANT
        );
    }

    /**
     * Retrieves all messages from a conversation.
     *
     * This method retrieves the complete message history for a specified conversation,
     * ordered chronologically by creation time.
     *
     * @param conversationId the ID of the conversation
     * @return a list of message responses in chronological order
     * @throws IllegalArgumentException if conversationId is blank
     * @throws com.ai_document_knowledge_assistant.exception.ResourceNotFoundException
     *         if the conversation does not exist
     */
    @Override
    @Transactional(readOnly = true)
    public List<ConversationMessageResponse> getMessages(
            final String conversationId
    ) {
        // Validate conversation ID and ensure conversation exists
        conversationValidationService.validateConversationIdNotBlank(conversationId);
        conversationValidationService.getConversationByIdOrThrow(conversationId);

        return conversationRepoService
                .findMessagesByConversationId(conversationId)
                .stream()
                .map(conversationMapper::toResponse)
                .toList();
    }


    /**
     * Creates and persists a message to the conversation.
     *
     * This is an internal method that handles the core logic of validating input,
     * ensuring the conversation exists, creating the message entity, and persisting it.
     *
     * @param conversationId the ID of the conversation
     * @param content the message content
     * @param role the role of the message sender (USER or ASSISTANT)
     * @return a response object containing the saved message details
     * @throws IllegalArgumentException if conversationId or content is blank
     * @throws com.ai_document_knowledge_assistant.exception.ResourceNotFoundException
     *         if the conversation does not exist
     */
    private ConversationMessageResponse createAndPersistMessage(
            final String conversationId,
            final String content,
            final MessageRole role
    ) {
        // Validate inputs
        conversationValidationService.validateConversationIdNotBlank(conversationId);
        conversationValidationService.validateMessageContentNotBlank(content);

        // Ensure conversation exists
        conversationValidationService.getConversationByIdOrThrow(conversationId);

        // Create and persist the message
        ConversationMessageEntity messageEntity = createMessageEntity(
                conversationId,
                content,
                role
        );

        ConversationMessageEntity savedMessage =
                conversationRepoService.saveMessage(messageEntity);

        return conversationMapper.toResponse(savedMessage);
    }

    /**
     * Creates a new ConversationMessageEntity with the provided details.
     *
     * @param conversationId the conversation ID
     * @param content the message content
     * @param role the message role (USER or ASSISTANT)
     * @return a new ConversationMessageEntity
     */
    private ConversationMessageEntity createMessageEntity(
            final String conversationId,
            final String content,
            final MessageRole role
    ) {
        ConversationMessageEntity message = messageEntityFactoryProvider
                .getIfAvailable(() -> ConversationMessageEntity::new)
                .get();
        message.setConversationId(conversationId);
        message.setRole(role);
        message.setContent(content);
        return message;
    }

}

