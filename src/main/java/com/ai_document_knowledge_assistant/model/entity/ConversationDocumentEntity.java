package com.ai_document_knowledge_assistant.model.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "conversation_documents",
        indexes = {
                @Index(
                        name = "idx_conversation_document_conversation",
                        columnList = "conversation_id"
                ),
                @Index(
                        name = "idx_conversation_document_document",
                        columnList = "document_id"
                ),
                @Index(
                        name = "uk_conversation_document",
                        columnList = "conversation_id, document_id",
                        unique = true
                )
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversationDocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(
            name = "conversation_id",
            nullable = false,
            length = 100
    )
    private String conversationId;

    @Column(
            name = "document_id",
            nullable = false,
            length = 100
    )
    private String documentId;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}