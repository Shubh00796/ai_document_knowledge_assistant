package com.ai_document_knowledge_assistant.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MessageRequest(

        @NotBlank(message = "Content cannot be blank")
        String content

) {
}