package com.ai_document_knowledge_assistant.service;



import com.ai_document_knowledge_assistant.model.VectorDocument;
import com.ai_document_knowledge_assistant.model.VectorSearchResult;

import java.util.List;

/**
 * Stores and searches vector documents.
 */
public interface VectorStore {

    /** Saves a vector document. */
    void save(VectorDocument document);

    /** Searches for matching documents. */
    List<VectorSearchResult> search(
            List<String> documentIds,
            List<Float> queryVector,
            int topK
    );

    /** Finds neighboring chunks for a document. */
    List<VectorDocument> findNeighbors(
            String documentId,
            int chunkIndex,
            int radius
    );

    /** Deletes all vectors for a document. */
    void deleteByDocumentId(String documentId);

    /** Clears all stored vectors. */
    void clear();

    /** Returns the number of stored vectors. */
    int size();
}