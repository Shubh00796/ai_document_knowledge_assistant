package com.ai_document_knowledge_assistant.reposiotry_ai;

import com.ai_document_knowledge_assistant.model.entity.DocumentChunkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentChunkRepository
        extends JpaRepository<DocumentChunkEntity, String> {

    List<DocumentChunkEntity> findByDocumentIdOrderByChunkIndex(
            String documentId
    );

    @Query("SELECT c FROM DocumentChunkEntity c")
    List<DocumentChunkEntity> findAllChunks();

    @Query("""
            SELECT c
            FROM DocumentChunkEntity c
            WHERE c.documentId = :documentId
            AND c.chunkIndex BETWEEN :startIndex AND :endIndex
            ORDER BY c.chunkIndex
            """)
    List<DocumentChunkEntity> findChunksAround(
            @Param("documentId") String documentId,
            @Param("startIndex") int startIndex,
            @Param("endIndex") int endIndex
    );

    void deleteByDocumentId(String documentId);
}