package com.ai_document_knowledge_assistant.controller;

import com.ai_document_knowledge_assistant.model.Embedding;
import com.ai_document_knowledge_assistant.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/embeddings")
@RequiredArgsConstructor
public class EmbeddingController {

    private final EmbeddingService embeddingService;


    @PostMapping
    public Embedding generateEmbedding(
            @RequestBody String text
    ) {
        return embeddingService.embed(text);
    }
}