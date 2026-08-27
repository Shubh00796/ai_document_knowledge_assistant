package com.ai_document_knowledge_assistant.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record RagRequest(

        @NotEmpty(message = "At least one document ID is required")
        List<@NotBlank(message = "Document ID cannot be blank") String> documentIds,

        @NotBlank(message = "Question cannot be blank")
        String question

) {
}