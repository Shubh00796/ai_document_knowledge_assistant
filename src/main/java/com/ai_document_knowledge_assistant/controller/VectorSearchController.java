package com.ai_document_knowledge_assistant.controller;
import com.ai_document_knowledge_assistant.dto.request.VectorSearchRequest;
import com.ai_document_knowledge_assistant.dto.responce.VectorSearchResponse;
import com.ai_document_knowledge_assistant.model.Embedding;
import com.ai_document_knowledge_assistant.model.VectorSearchResult;
import com.ai_document_knowledge_assistant.service.EmbeddingService;
import com.ai_document_knowledge_assistant.service.VectorStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vector-search")
public class VectorSearchController {

    private final EmbeddingService embeddingService;
    private final VectorStore vectorStore;

    public VectorSearchController(
            EmbeddingService embeddingService,
            VectorStore vectorStore
    ) {
        this.embeddingService = embeddingService;
        this.vectorStore = vectorStore;
    }

    @PostMapping
    public List<VectorSearchResponse> search(
            @RequestBody VectorSearchRequest request
    ) {
        Embedding queryEmbedding =
                embeddingService.embed(request.query());

        return vectorStore.search(
                        request.documentId(),
                        queryEmbedding.vector(),
                        request.topK()
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private VectorSearchResponse toResponse(
            VectorSearchResult result
    ) {
        return new VectorSearchResponse(
                result.document().documentId(),
                result.document().chunkIndex(),
                result.document().content(),
                result.similarity()
        );
    }
}