package com.ai_document_knowledge_assistant.reposiotry_ai;

import com.ai_document_knowledge_assistant.exception.ResourceNotFoundException;
import com.ai_document_knowledge_assistant.model.entity.DocumentChunkEntity;
import com.ai_document_knowledge_assistant.model.entity.DocumentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentRepoService {

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository documentChunkRepository;

    public DocumentEntity saveDocument(
            DocumentEntity document
    ) {
        return documentRepository.save(document);
    }

    public DocumentEntity findByContentHash(
            String contentHash
    ) {
        return documentRepository.findByContentHash(contentHash)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Document with hash "
                                        + contentHash
                                        + " not found"
                        )
                );
    }

    public boolean existsByContentHash(
            String contentHash
    ) {
        return documentRepository.existsByContentHash(
                contentHash
        );
    }

    public DocumentEntity findByDocumentId(
            String documentId
    ) {
        return documentRepository.findByDocumentId(documentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Document with ID "
                                        + documentId
                                        + " not found"
                        )
                );
    }

    public List<DocumentChunkEntity> findAllChunks() {
        return documentChunkRepository.findAllChunks();
    }


    public List<DocumentChunkEntity> findChunksByDocumentId(
            String documentId
    ) {
        return documentChunkRepository
                .findByDocumentIdOrderByChunkIndex(documentId);
    }

    public void deleteChunksByDocumentId(
            String documentId
    ) {
        documentChunkRepository.deleteByDocumentId(documentId);
    }

    public DocumentChunkEntity saveChunk(
            DocumentChunkEntity entity
    ) {
        return documentChunkRepository.save(entity);
    }

    public List<DocumentChunkEntity> findChunksAround(
            String documentId,
            int chunkIndex,
            int radius
    ) {

        if (documentId == null || documentId.isBlank()) {
            throw new IllegalArgumentException(
                    "Document ID cannot be blank"
            );
        }

        if (radius < 0) {
            throw new IllegalArgumentException(
                    "Radius cannot be negative"
            );
        }

        return documentChunkRepository.findChunksAround(
                documentId,
                chunkIndex - radius,
                chunkIndex + radius
        );

    }
}