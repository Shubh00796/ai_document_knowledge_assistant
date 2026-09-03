package com.ai_document_knowledge_assistant.reposiotry_ai;


import com.ai_document_knowledge_assistant.model.entity.ConversationDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationDocumentRepository
        extends JpaRepository<ConversationDocumentEntity, String> {

    List<ConversationDocumentEntity> findByConversationId(
            String conversationId
    );

    boolean existsByConversationIdAndDocumentId(
            String conversationId,
            String documentId
    );

    void deleteByConversationIdAndDocumentId(
            String conversationId,
            String documentId
    );
}