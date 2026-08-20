package com.ai_document_knowledge_assistant.service.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ai_document_knowledge_assistant.model.VectorDocument;
import com.ai_document_knowledge_assistant.model.entity.DocumentChunkEntity;
import com.ai_document_knowledge_assistant.reposiotry.DocumentRepoService;
import com.ai_document_knowledge_assistant.service.VectorStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class VectorStoreLoader {

    private final DocumentRepoService documentRepoService;
    private final VectorStore vectorStore;
    private final ObjectMapper objectMapper;
    private final AtomicBoolean startupLoadDone = new AtomicBoolean(false);

    @EventListener(ApplicationReadyEvent.class)
    public void loadVectorStore() {

        if (!startupLoadDone.compareAndSet(false, true)) {
            log.info("Vector store loader already executed. Skipping duplicate startup load.");
            return;
        }

        log.info("Loading vector store from MySQL document chunks...");

        long start = System.currentTimeMillis();

        List<DocumentChunkEntity> chunks =
                documentRepoService.findAllChunks();

        int loaded = 0;

        for (DocumentChunkEntity chunk : chunks) {

            VectorDocument vectorDocument =
                    toVectorDocument(chunk);

            vectorStore.save(vectorDocument);

            loaded++;
        }

        long duration =
                System.currentTimeMillis() - start;

        log.info(
                "Vector store loading completed. Chunks loaded: {}, time: {} ms, in-memory size: {}",
                loaded,
                duration,
                vectorStore.size()
        );
    }

    private VectorDocument toVectorDocument(
            DocumentChunkEntity entity
    ) {

        List<Float> embedding =
                deserializeEmbedding(entity.getEmbedding());

        return new VectorDocument(
                entity.getId(),
                entity.getDocumentId(),
                entity.getChunkIndex(),
                entity.getContent(),
                embedding
        );
    }

    private List<Float> deserializeEmbedding(
            String embeddingJson
    ) {

        try {

            return objectMapper.readValue(
                    embeddingJson,
                    new TypeReference<List<Float>>() {}
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Failed to deserialize persisted chunk embedding JSON",
                    exception
            );
        }
    }
}