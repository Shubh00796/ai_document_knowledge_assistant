package com.ai_document_knowledge_assistant.service.serviceImpl;


import com.ai_document_knowledge_assistant.exception.VectorStoreException;
import com.ai_document_knowledge_assistant.model.VectorDocument;
import com.ai_document_knowledge_assistant.model.VectorSearchResult;
import com.ai_document_knowledge_assistant.service.VectorStore;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryVectorStore implements VectorStore {

    private final Map<String, VectorDocument> documents =
            new ConcurrentHashMap<>();

    @Override
    public void save(VectorDocument document) {
        validateDocument(document);

        documents.put(document.id(), document);
    }

    @Override
    public List<VectorSearchResult> search(
            List<String> documentIds,
            List<Float> queryVector,
            int topK
    ) {
        validateEmbedding(queryVector);
        validateTopK(topK);

        return documents.values()
                .stream()
                .filter(document ->
                        documentIds != null && !documentIds.isEmpty()
                                && documentIds.contains(document.documentId())
                )
                .map(document -> new VectorSearchResult(
                        document.documentId(),
                        document,
                        cosineSimilarity(
                                queryVector,
                                document.embedding()
                        )
                ))
                .sorted(
                        Comparator.comparingDouble(
                                VectorSearchResult::similarity
                        ).reversed()
                )
                .limit(topK)
                .toList();
    }



    @Override
    public void deleteByDocumentId(String documentId) {
        throwIfInvalid(documentId == null || documentId.isBlank(), "Document ID cannot be blank");

        documents.values().removeIf(
                document -> document.documentId().equals(documentId)
        );
    }

    @Override
    public int size() {
        return documents.size();
    }

    @Override
    public void clear() {
        documents.clear();
    }





    private double cosineSimilarity(
            List<Float> first,
            List<Float> second
    ) {
        throwIfInvalid(first.size() != second.size(), "Vector dimensions must match");

        double dotProduct = 0.0;
        double firstMagnitude = 0.0;
        double secondMagnitude = 0.0;

        for (int i = 0; i < first.size(); i++) {
            double firstValue = first.get(i);
            double secondValue = second.get(i);

            dotProduct += firstValue * secondValue;
            firstMagnitude += firstValue * firstValue;
            secondMagnitude += secondValue * secondValue;
        }

        if (firstMagnitude == 0.0 || secondMagnitude == 0.0) {
            return 0.0;
        }

        return dotProduct /
                (Math.sqrt(firstMagnitude)
                        * Math.sqrt(secondMagnitude));
    }




    private void validateDocument(VectorDocument document) {
        throwIfInvalid(document == null, "Vector document cannot be null");

        validateEmbedding(document.embedding());
    }

    private void validateEmbedding(List<Float> embedding) {
        throwIfInvalid(embedding == null || embedding.isEmpty(), "Embedding cannot be null or empty");
    }

    private static void validateTopK(int topK) {
        throwIfInvalid(topK <= 0, "topK must be greater than zero");
    }

    private static void throwIfInvalid(boolean invalid, String message) {
        if (invalid) {
            throw new VectorStoreException(
                    message
            );
        }
    }

    @Override
    public List<VectorDocument> findNeighbors(
            String documentId,
            int chunkIndex,
            int radius
    ) {

        return documents.values()
                .stream()
                .filter(
                        document ->
                                document.documentId()
                                        .equals(documentId)
                )
                .filter(
                        document ->
                                Math.abs(
                                        document.chunkIndex()
                                                - chunkIndex
                                ) <= radius
                )
                .sorted(
                        Comparator.comparingInt(
                                VectorDocument::chunkIndex
                        )
                )
                .toList();
    }


}