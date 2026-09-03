package com.ai_document_knowledge_assistant.service;



import com.ai_document_knowledge_assistant.dto.request.MessageRequest;
import com.ai_document_knowledge_assistant.dto.responce.ConversationMessageResponse;
import com.ai_document_knowledge_assistant.dto.responce.ConversationResponse;

import java.util.List;

/**
 * Manages conversations and messages.
 */
public interface ConversationService {

    /** Creates a new conversation. */
    ConversationResponse createConversation();

    /** Adds a user message to a conversation. */
    ConversationMessageResponse addUserMessage(
            String conversationId,
            MessageRequest request
    );

    /** Adds an assistant message to a conversation. */
    ConversationMessageResponse addAssistantMessage(
            String conversationId,
            MessageRequest request
    );

    /** Returns the messages for a conversation. */
    List<ConversationMessageResponse> getMessages(
            String conversationId
    );
}