package com.ai_document_knowledge_assistant.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * RAG question request.
 */
public record RagRequest(

        @NotBlank(message = "Conversation ID cannot be blank")
        String conversationId,



        @NotBlank(message = "Question cannot be blank")
        String question

) {
}