package com.ai_document_knowledge_assistant.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Stored document chunk.
 */
@Entity
@Table(
        name = "document_chunks",
        indexes = {
                @Index(
                        name = "idx_chunk_document",
                        columnList = "document_id"
                ),
                @Index(
                        name = "uk_chunk_document_index",
                        columnList = "document_id, chunk_index",
                        unique = true
                )
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentChunkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(
            name = "document_id",
            nullable = false,
            length = 100
    )
    private String documentId;

    @Column(
            name = "chunk_index",
            nullable = false
    )
    private Integer chunkIndex;

    @Lob
    @Column(
            name = "content",
            nullable = false,
            columnDefinition = "LONGTEXT"
    )
    private String content;

    @Lob
    @Column(
            name = "embedding",
            nullable = false,
            columnDefinition = "LONGTEXT"
    )
    private String embedding;
}