package com.ai_document_knowledge_assistant.controller;

import com.ai_document_knowledge_assistant.model.DocumentChunk;
import com.ai_document_knowledge_assistant.service.serviceImpl.DocumentEmbeddingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentIndexController {

    private final DocumentEmbeddingService documentEmbeddingService;

    public DocumentIndexController(
            DocumentEmbeddingService documentEmbeddingService
    ) {
        this.documentEmbeddingService = documentEmbeddingService;
    }

    @PostMapping("/{documentId}/index")
    public ResponseEntity<Void> indexDocument(
            @PathVariable String documentId,
            @RequestBody List<DocumentChunk> chunks
    ) {

        documentEmbeddingService.indexDocument(
                documentId,
                chunks
        );

        return ResponseEntity.accepted().build();
    }
}