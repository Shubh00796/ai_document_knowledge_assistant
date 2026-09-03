package com.ai_document_knowledge_assistant.service;


import com.ai_document_knowledge_assistant.dto.responce.DocumentUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Handles document upload operations.
 */
public interface DocumentService {

    /** Uploads a document file. */
    DocumentUploadResponse upload(MultipartFile file) throws IOException;
}