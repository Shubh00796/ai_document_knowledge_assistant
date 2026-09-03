package com.ai_document_knowledge_assistant.dto.responce;



import java.util.List;

/**
 * Document upload result.
 */
public record DocumentUploadResponse(
        String fileName,
        String contentType,
        int pageCount,
        int characterCount,
        int chunkCount,
        List<String> chunks
) {
}