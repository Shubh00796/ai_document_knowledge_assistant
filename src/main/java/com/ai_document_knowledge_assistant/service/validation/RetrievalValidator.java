package com.ai_document_knowledge_assistant.service.validation;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RetrievalValidator {

    public void validate(
            List<String> documentIds,
            String question,
            int topK
    ) {

        validateQuestion(question);
        validateTopK(topK);
        validateDocumentIds(documentIds);
    }

    private void validateQuestion(
            String question
    ) {

        if (question == null || question.isBlank()) {

            throw new IllegalArgumentException(
                    "Question cannot be blank"
            );
        }
    }

    private void validateTopK(
            int topK
    ) {

        if (topK <= 0) {

            throw new IllegalArgumentException(
                    "topK must be greater than zero"
            );
        }
    }

    private void validateDocumentIds(
            List<String> documentIds
    ) {

        if (documentIds == null ||
                documentIds.isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one document ID is required"
            );
        }

        if (documentIds.stream()
                .anyMatch(
                        id -> id == null || id.isBlank()
                )) {

            throw new IllegalArgumentException(
                    "Document ID cannot be blank"
            );
        }
    }
}