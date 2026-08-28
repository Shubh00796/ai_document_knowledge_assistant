package com.ai_document_knowledge_assistant.reposiotry_ai;


import com.ai_document_knowledge_assistant.model.entity.ConversationMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationMessageRepository
        extends JpaRepository<ConversationMessageEntity, String> {

    List<ConversationMessageEntity>
    findByConversationIdOrderByCreatedAtAsc(
            String conversationId
    );
}