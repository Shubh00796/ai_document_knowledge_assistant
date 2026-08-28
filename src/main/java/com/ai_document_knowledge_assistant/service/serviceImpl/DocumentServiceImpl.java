package com.ai_document_knowledge_assistant.service.serviceImpl;

import com.ai_document_knowledge_assistant.dto.responce.DocumentUploadResponse;
import com.ai_document_knowledge_assistant.model.DocumentChunk;
import com.ai_document_knowledge_assistant.model.ParsedDocument;
import com.ai_document_knowledge_assistant.model.entity.DocumentChunkEntity;
import com.ai_document_knowledge_assistant.model.entity.DocumentEntity;
import com.ai_document_knowledge_assistant.parcer.DocumentParser;
import com.ai_document_knowledge_assistant.parcer.DocumentParserFactory;
import com.ai_document_knowledge_assistant.reposiotry_ai.DocumentRepoService;
import com.ai_document_knowledge_assistant.service.ChunkingService;
import com.ai_document_knowledge_assistant.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl
        implements DocumentService {

    private final DocumentParserFactory documentParserFactory;
    private final ChunkingService chunkingService;
    private final DocumentEmbeddingService documentEmbeddingService;
    private final DocumentRepoService documentRepoService;
    private final DocumentHashService documentHashService;

    @Override
    public DocumentUploadResponse upload(
            MultipartFile file
    ) throws IOException {

        validateFile(file);

        /*
         * Read the original file bytes once.
         */
        byte[] fileBytes =
                file.getBytes();

        /*
         * Generate SHA-256 fingerprint.
         */
        String contentHash =
                documentHashService.sha256(
                        fileBytes
                );

        /*
         * Check whether this exact file has
         * already been processed.
         */
        if (documentRepoService.existsByContentHash(
                contentHash
        )) {

            DocumentEntity existingDocument =
                    documentRepoService.findByContentHash(
                            contentHash
                    );

            return buildExistingDocumentResponse(
                    existingDocument
            );
        }

        /*
         * New document.
         */
        DocumentParser parser =
                documentParserFactory.getParser(
                        file
                );

        ParsedDocument parsedDocument =
                parser.parse(file);

        /*
         * Chunk document.
         */
        List<DocumentChunk> chunks =
                chunkingService.chunk(
                        parsedDocument
                );

        /*
         * Generate application-level document ID.
         */
        String documentId =
                UUID.randomUUID().toString();

        /*
         * Save document metadata FIRST.
         */
        DocumentEntity document =
                new DocumentEntity();

        document.setDocumentId(
                documentId
        );

        document.setFileName(
                parsedDocument.fileName()
        );

        document.setContentType(
                parsedDocument.contentType()
        );

        document.setPageCount(
                parsedDocument.pageCount()
        );

        document.setContentHash(
                contentHash
        );

        documentRepoService.saveDocument(
                document
        );

        /*
         * Generate embeddings and persist chunks.
         */
        documentEmbeddingService.indexDocument(
                documentId,
                chunks
        );

        /*
         * Build response.
         */
        List<String> chunkContents =
                chunks.stream()
                        .map(DocumentChunk::content)
                        .toList();

        return new DocumentUploadResponse(
                parsedDocument.fileName(),
                parsedDocument.contentType(),
                parsedDocument.pageCount(),
                parsedDocument.text().length(),
                chunks.size(),
                chunkContents
        );
    }

    private DocumentUploadResponse
    buildExistingDocumentResponse(
            DocumentEntity document
    ) {

        List<DocumentChunkEntity> chunks =
                documentRepoService
                        .findChunksByDocumentId(
                                document.getDocumentId()
                        );

        List<String> chunkContents =
                chunks.stream()
                        .map(
                                DocumentChunkEntity::getContent
                        )
                        .toList();

        /*
         * Notice:
         *
         * We DO NOT call:
         *
         * parser.parse(...)
         * chunkingService.chunk(...)
         * embeddingService.embed(...)
         *
         * because the document is already processed.
         */
        return new DocumentUploadResponse(
                document.getFileName(),
                document.getContentType(),
                document.getPageCount(),
                0,
                chunkContents.size(),
                chunkContents
        );
    }

    private void validateFile(
            MultipartFile file
    ) {

        if (file == null || file.isEmpty()) {

            throw new IllegalArgumentException(
                    "Document file cannot be empty"
            );
        }
    }
}