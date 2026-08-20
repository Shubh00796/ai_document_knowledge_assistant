package com.ai_document_knowledge_assistant.service;


import com.ai_document_knowledge_assistant.model.VectorSearchResult;

import java.util.List;

public interface RetrievalService {

    List<VectorSearchResult> retrieve(
            String question,
            int topK
    );
}