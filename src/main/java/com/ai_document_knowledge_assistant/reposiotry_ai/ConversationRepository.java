package com.ai_document_knowledge_assistant.reposiotry_ai;


import com.ai_document_knowledge_assistant.model.entity.ConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository
        extends JpaRepository<ConversationEntity, String> {
}