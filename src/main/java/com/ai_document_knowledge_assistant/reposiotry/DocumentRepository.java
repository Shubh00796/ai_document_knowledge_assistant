package com.ai_document_knowledge_assistant.reposiotry;



import com.ai_document_knowledge_assistant.model.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository
        extends JpaRepository<DocumentEntity, String> {

    Optional<DocumentEntity> findByContentHash(String contentHash);

    boolean existsByContentHash(String contentHash);

    Optional<DocumentEntity> findByDocumentId(String documentId);

    boolean existsByDocumentId(String documentId);
}