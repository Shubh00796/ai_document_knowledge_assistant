package com.ai_document_knowledge_assistant.dto.request;


import jakarta.validation.constraints.NotBlank;

public record AttachDocumentRequest(

        @NotBlank(message = "Document ID is required")
        String documentId

) {
}