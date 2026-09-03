package com.ai_document_knowledge_assistant.service;


import com.ai_document_knowledge_assistant.model.VectorSearchResult;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * Retrieves relevant vector search results.
 */
public interface RetrievalService {

    /** Retrieves matching results for a question. */
    List<VectorSearchResult> retrieve(
            List<String> documentIds,
            String question,
            int topK
    );
}