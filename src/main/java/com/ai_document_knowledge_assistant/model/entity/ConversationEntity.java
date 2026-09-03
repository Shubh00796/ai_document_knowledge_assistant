package com.ai_document_knowledge_assistant.model.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Conversation record.
 */
@Entity
@Table(
        name = "conversations",
        indexes = {
                @Index(
                        name = "idx_conversation_created_at",
                        columnList = "created_at"
                )
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

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