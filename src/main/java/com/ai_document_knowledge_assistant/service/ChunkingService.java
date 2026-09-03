package com.ai_document_knowledge_assistant.service;



import com.ai_document_knowledge_assistant.model.DocumentChunk;
import com.ai_document_knowledge_assistant.model.ParsedDocument;

import java.util.List;

/**
 * Splits parsed documents into chunks.
 */
public interface ChunkingService {

    /** Chunks a parsed document. */
    List<DocumentChunk> chunk(ParsedDocument document);
}