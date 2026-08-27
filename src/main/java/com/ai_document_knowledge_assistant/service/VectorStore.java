package com.ai_document_knowledge_assistant.service;



import com.ai_document_knowledge_assistant.model.VectorDocument;
import com.ai_document_knowledge_assistant.model.VectorSearchResult;

import java.util.List;

public interface VectorStore {

    void save(VectorDocument document);

    List<VectorSearchResult> search(
            List<String> documentIds,
            List<Float> queryVector,
            int topK
    );

    List<VectorDocument> findNeighbors(

            String documentId,
            int chunkIndex,
            int radius
    );

    void deleteByDocumentId(String documentId);

    void clear();

    int size();
}