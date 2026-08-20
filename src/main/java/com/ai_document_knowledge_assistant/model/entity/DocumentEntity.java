package com.ai_document_knowledge_assistant.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "documents",
        indexes = {
                @Index(
                        name = "idx_document_hash",
                        columnList = "content_hash",
                        unique = true
                ),
                @Index(
                        name = "idx_document_document_id",
                        columnList = "document_id",
                        unique = true
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Application-level document identifier.
     *
     * This is the ID used by:
     * - document_chunks.document_id
     * - VectorDocument.documentId
     * - RAG document filtering
     */
    @Column(
            name = "document_id",
            nullable = false,
            unique = true,
            length = 100
    )
    private String documentId;

    @Column(
            name = "file_name",
            nullable = false
    )
    private String fileName;

    @Column(
            name = "content_type",
            nullable = false,
            length = 100
    )
    private String contentType;

    @Column(name = "page_count")
    private Integer pageCount;

    /**
     * SHA-256 hash of the original uploaded file.
     *
     * Used to detect whether this exact file
     * has already been processed.
     */
    @Column(
            name = "content_hash",
            nullable = false,
            unique = true,
            length = 64
    )
    private String contentHash;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
    }
}