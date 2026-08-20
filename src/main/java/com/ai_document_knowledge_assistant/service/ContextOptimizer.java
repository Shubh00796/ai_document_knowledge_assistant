package com.ai_document_knowledge_assistant.service;


import com.ai_document_knowledge_assistant.model.RetrievedChunk;

import java.util.List;

public interface ContextOptimizer {

    List<RetrievedChunk> optimize(
            List<RetrievedChunk> retrievedChunks
    );
}