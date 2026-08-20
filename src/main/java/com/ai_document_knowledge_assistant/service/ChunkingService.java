package com.ai_document_knowledge_assistant.service;



import com.ai_document_knowledge_assistant.model.DocumentChunk;
import com.ai_document_knowledge_assistant.model.ParsedDocument;

import java.util.List;

public interface ChunkingService {

    List<DocumentChunk> chunk(ParsedDocument document);
}