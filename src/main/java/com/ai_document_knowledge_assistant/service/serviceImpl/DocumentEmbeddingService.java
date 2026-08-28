package com.ai_document_knowledge_assistant.service.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ai_document_knowledge_assistant.exception.VectorStoreException;
import com.ai_document_knowledge_assistant.model.DocumentChunk;
import com.ai_document_knowledge_assistant.model.Embedding;
import com.ai_document_knowledge_assistant.model.VectorDocument;
import com.ai_document_knowledge_assistant.model.entity.DocumentChunkEntity;
import com.ai_document_knowledge_assistant.reposiotry_ai.DocumentRepoService;
import com.ai_document_knowledge_assistant.service.EmbeddingService;
import com.ai_document_knowledge_assistant.service.VectorStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentEmbeddingService {

    private final EmbeddingService embeddingService;
    private final VectorStore vectorStore;
    private final DocumentRepoService documentRepoService;
    private final ObjectMapper objectMapper;

    public void indexDocument(
            String documentId,
            List<DocumentChunk> chunks
    ) {

        validateDocumentId(documentId);

        if (chunks == null || chunks.isEmpty()) {
            return;
        }

        for (DocumentChunk chunk : chunks) {

            VectorDocument vectorDocument =
                    createVectorDocument(
                            documentId,
                            chunk
                    );

            /*
             * Keep the vector in memory so the newly uploaded
             * document can immediately participate in RAG.
             */
            vectorStore.save(vectorDocument);

            /*
             * Persist the chunk so it survives application restart.
             */
            persistChunk(
                    documentId,
                    vectorDocument
            );
        }
    }

    public void removeDocument(
            String documentId
    ) {

        validateDocumentId(documentId);

        vectorStore.deleteByDocumentId(
                documentId
        );

        documentRepoService.deleteChunksByDocumentId(
                documentId
        );
    }

    private VectorDocument createVectorDocument(
            String documentId,
            DocumentChunk chunk
    ) {

        Embedding embedding =
                embeddingService.embed(
                        chunk.content()
                );

        return new VectorDocument(
                UUID.randomUUID().toString(),
                documentId,
                chunk.chunkIndex(),
                chunk.content(),
                embedding.vector()
        );
    }

    private void persistChunk(
            String documentId,
            VectorDocument vectorDocument
    ) {

        DocumentChunkEntity entity =
                DocumentChunkEntity.builder()
                        .documentId(documentId)
                        .chunkIndex(
                                vectorDocument.chunkIndex()
                        )
                        .content(
                                vectorDocument.content()
                        )
                        .embedding(
                                serializeEmbedding(
                                        vectorDocument.embedding()
                                )
                        )
                        .build();

        documentRepoService.saveChunk(entity);
    }

    private String serializeEmbedding(
            List<Float> embedding
    ) {

        try {

            return objectMapper.writeValueAsString(
                    embedding
            );

        } catch (JsonProcessingException exception) {

            throw new VectorStoreException(
                    "Failed to serialize document embedding",
                    exception
            );
        }
    }

    private void validateDocumentId(
            String documentId
    ) {

        if (documentId == null || documentId.isBlank()) {

            throw new VectorStoreException(
                    "Document ID cannot be blank"
            );
        }
    }
}