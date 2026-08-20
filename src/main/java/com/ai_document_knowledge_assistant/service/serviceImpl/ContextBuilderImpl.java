package com.ai_document_knowledge_assistant.service.serviceImpl;


import com.ai_document_knowledge_assistant.model.RetrievedChunk;
import com.ai_document_knowledge_assistant.service.ContextBuilder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContextBuilderImpl implements ContextBuilder {

    @Override
    public String build(
            List<RetrievedChunk> chunks
    ) {

        if (chunks == null || chunks.isEmpty()) {
            return "";
        }

        /*
         * Preserve retrieval information but arrange
         * chunks in document order so the LLM sees
         * neighboring content naturally.
         */
        List<RetrievedChunk> orderedChunks =
                chunks.stream()
                        .sorted(
                                Comparator
                                        .comparing(
                                                RetrievedChunk::documentId
                                        )
                                        .thenComparing(
                                                RetrievedChunk::chunkIndex
                                        )
                        )
                        .toList();

        return orderedChunks.stream()
                .map(this::formatChunk)
                .collect(Collectors.joining("\n\n"));
    }

    private String formatChunk(
            RetrievedChunk chunk
    ) {

        return """
                [Source]
                Document ID: %s
                Chunk: %d
                Similarity: %.4f

                %s
                """.formatted(
                chunk.documentId(),
                chunk.chunkIndex(),
                chunk.similarity(),
                chunk.content()
        );
    }
}