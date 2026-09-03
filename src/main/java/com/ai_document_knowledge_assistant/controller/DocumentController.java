package com.ai_document_knowledge_assistant.controller;


import com.ai_document_knowledge_assistant.dto.responce.DocumentUploadResponse;
import com.ai_document_knowledge_assistant.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Handles document upload endpoints.
 */
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<DocumentUploadResponse> upload(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        DocumentUploadResponse response = documentService.upload(file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}