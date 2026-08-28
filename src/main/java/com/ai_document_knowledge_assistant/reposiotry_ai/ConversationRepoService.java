package com.ai_document_knowledge_assistant.reposiotry_ai;


import com.ai_document_knowledge_assistant.exception.ResourceNotFoundException;
import com.ai_document_knowledge_assistant.model.entity.ConversationEntity;
import com.ai_document_knowledge_assistant.model.entity.ConversationMessageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConversationRepoService {

    private final ConversationRepository conversationRepository;
    private final ConversationMessageRepository conversationMessageRepository;


    public ConversationEntity saveConversation(
            ConversationEntity conversation
    ) {
        return conversationRepository.save(conversation);
    }


    public ConversationEntity findByConversationId(
            String conversationId
    ) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Conversation with ID "
                                        + conversationId
                                        + " not found"
                        )
                );
    }


    public boolean existsByConversationId(
            String conversationId
    ) {
        return conversationRepository.existsById(conversationId);
    }


    public void deleteConversation(
            String conversationId
    ) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new ResourceNotFoundException(
                    "Conversation with ID "
                            + conversationId
                            + " not found"
            );
        }

        conversationRepository.deleteById(conversationId);
    }


    public List<ConversationMessageEntity> findMessagesByConversationId(
            String conversationId
    ) {
        return conversationMessageRepository
                .findByConversationIdOrderByCreatedAtAsc(
                        conversationId
                );
    }


    public ConversationMessageEntity saveMessage(
            ConversationMessageEntity message
    ) {
        return conversationMessageRepository.save(message);
    }
}