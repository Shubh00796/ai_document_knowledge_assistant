package com.ai_document_knowledge_assistant.service;



import com.ai_document_knowledge_assistant.dto.request.MessageRequest;
import com.ai_document_knowledge_assistant.dto.responce.ConversationMessageResponse;
import com.ai_document_knowledge_assistant.dto.responce.ConversationResponse;

import java.util.List;

public interface ConversationService {

    ConversationResponse createConversation();

    ConversationMessageResponse addUserMessage(
            String conversationId,
            MessageRequest request
    );

    ConversationMessageResponse addAssistantMessage(
            String conversationId,
            MessageRequest request
    );

    List<ConversationMessageResponse> getMessages(
            String conversationId
    );
}