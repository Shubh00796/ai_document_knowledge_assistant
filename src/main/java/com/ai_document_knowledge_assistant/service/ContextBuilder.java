package com.ai_document_knowledge_assistant.service;


import com.ai_document_knowledge_assistant.model.RetrievedChunk;

import java.util.List;

public interface ContextBuilder {

    String build(
            List<RetrievedChunk> chunks
    );
}