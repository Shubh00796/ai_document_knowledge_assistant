package com.ai_document_knowledge_assistant.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RagRequest(

        @NotBlank(message = "Question cannot be blank")
        String question

) {
}