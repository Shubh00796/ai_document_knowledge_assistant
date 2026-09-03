package com.ai_document_knowledge_assistant.parcer;


import com.ai_document_knowledge_assistant.model.ParsedDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Parses uploaded documents.
 */
public interface DocumentParser {

    boolean supports(MultipartFile file);

    ParsedDocument parse(MultipartFile file);
}