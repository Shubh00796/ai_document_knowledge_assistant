package com.ai_document_knowledge_assistant.service.serviceImpl;

import com.ai_document_knowledge_assistant.model.RetrievedChunk;
import com.ai_document_knowledge_assistant.model.entity.DocumentChunkEntity;
import com.ai_document_knowledge_assistant.reposiotry.DocumentRepoService;
import com.ai_document_knowledge_assistant.service.ContextOptimizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ContextOptimizerImpl
        implements ContextOptimizer {

    private static final int NEIGHBOR_WINDOW = 1;

    private static final int MAX_CONTEXT_CHUNKS = 6;

    private static final int MAX_CONTEXT_CHARACTERS = 5000;

    private final DocumentRepoService documentRepoService;

    @Override
    public List<RetrievedChunk> optimize(
            List<RetrievedChunk> retrievedChunks
    ) {

        if (retrievedChunks == null ||
                retrievedChunks.isEmpty()) {

            return List.of();
        }

        /*
         * ---------------------------------------------------------
         * STEP 1
         *
         * Keep only the strongest semantic search results.
         *
         * These are the chunks that actually matched the
         * user's question through cosine similarity.
         * ---------------------------------------------------------
         */
        List<RetrievedChunk> topResults =
                retrievedChunks.stream()
                        .sorted(
                                Comparator.comparingDouble(
                                        RetrievedChunk::similarity
                                ).reversed()
                        )
                        .limit(5)
                        .toList();

        /*
         * ---------------------------------------------------------
         * STEP 2
         *
         * Store all candidates.
         *
         * Key:
         *
         * documentId + chunkIndex
         *
         * This prevents duplicate chunks.
         * ---------------------------------------------------------
         */
        Map<String, RetrievedChunk> allCandidates =
                new HashMap<>();

        /*
         * ---------------------------------------------------------
         * STEP 3
         *
         * Add semantic results first.
         * ---------------------------------------------------------
         */
        for (RetrievedChunk retrievedChunk : topResults) {

            allCandidates.put(
                    createKey(
                            retrievedChunk.documentId(),
                            retrievedChunk.chunkIndex()
                    ),
                    retrievedChunk
            );
        }

        /*
         * ---------------------------------------------------------
         * STEP 4
         *
         * Expand every semantic result with neighboring chunks.
         *
         * Example:
         *
         * semantic chunk = 76
         *
         * neighbor window = 1
         *
         * MySQL gives:
         *
         * 75
         * 76
         * 77
         *
         * ---------------------------------------------------------
         */
        for (RetrievedChunk retrievedChunk : topResults) {

            List<DocumentChunkEntity> neighbors =
                    documentRepoService.findChunksAround(
                            retrievedChunk.documentId(),
                            retrievedChunk.chunkIndex(),
                            NEIGHBOR_WINDOW
                    );

            for (DocumentChunkEntity neighbor : neighbors) {

                String key =
                        createKey(
                                neighbor.getDocumentId(),
                                neighbor.getChunkIndex()
                        );

                /*
                 * Do not overwrite a semantic result.
                 *
                 * Semantic result has a real similarity score.
                 *
                 * Neighbor has no similarity score, so we use 0.0.
                 */
                allCandidates.putIfAbsent(
                        key,
                        new RetrievedChunk(
                                neighbor.getDocumentId(),
                                neighbor.getId(),
                                neighbor.getChunkIndex(),
                                neighbor.getContent(),
                                0.0
                        )
                );
            }
        }

        /*
         * ---------------------------------------------------------
         * STEP 5
         *
         * Select context intelligently.
         *
         * IMPORTANT:
         *
         * We do NOT simply sort everything by chunkIndex.
         *
         * Semantic results get priority.
         *
         * Then neighbors are added if there is room.
         * ---------------------------------------------------------
         */
        List<RetrievedChunk> selected =
                selectContext(
                        topResults,
                        allCandidates
                );

        /*
         * ---------------------------------------------------------
         * STEP 6
         *
         * Finally arrange selected chunks in document order.
         *
         * This makes the context easier for the LLM to understand.
         * ---------------------------------------------------------
         */
        return selected.stream()
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
    }

    private List<RetrievedChunk> selectContext(
            List<RetrievedChunk> topResults,
            Map<String, RetrievedChunk> allCandidates
    ) {

        /*
         * LinkedHashMap preserves insertion order.
         *
         * We use it because semantic results must be selected
         * before neighbors.
         */
        Map<String, RetrievedChunk> selected =
                new LinkedHashMap<>();

        int characterCount = 0;

        /*
         * ---------------------------------------------------------
         * FIRST:
         *
         * Add strongest semantic results.
         * ---------------------------------------------------------
         */
        for (RetrievedChunk chunk : topResults) {

            if (selected.size() >= MAX_CONTEXT_CHUNKS) {
                break;
            }

            int nextCharacterCount =
                    characterCount +
                            chunk.content().length();

            if (nextCharacterCount >
                    MAX_CONTEXT_CHARACTERS) {

                continue;
            }

            String key =
                    createKey(
                            chunk.documentId(),
                            chunk.chunkIndex()
                    );

            selected.put(
                    key,
                    chunk
            );

            characterCount =
                    nextCharacterCount;
        }

        /*
         * ---------------------------------------------------------
         * SECOND:
         *
         * Add neighboring chunks.
         *
         * Neighbor chunks have similarity = 0.0.
         *
         * They are useful because they provide surrounding context.
         * ---------------------------------------------------------
         */
        List<RetrievedChunk> neighbors =
                allCandidates.values()
                        .stream()
                        .filter(
                                chunk ->
                                        !selected.containsKey(
                                                createKey(
                                                        chunk.documentId(),
                                                        chunk.chunkIndex()
                                                )
                                        )
                        )
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

        for (RetrievedChunk chunk : neighbors) {

            if (selected.size() >= MAX_CONTEXT_CHUNKS) {
                break;
            }

            int nextCharacterCount =
                    characterCount +
                            chunk.content().length();

            if (nextCharacterCount >
                    MAX_CONTEXT_CHARACTERS) {

                continue;
            }

            String key =
                    createKey(
                            chunk.documentId(),
                            chunk.chunkIndex()
                    );

            selected.put(
                    key,
                    chunk
            );

            characterCount =
                    nextCharacterCount;
        }

        return List.copyOf(
                selected.values()
        );
    }

    private String createKey(
            String documentId,
            int chunkIndex
    ) {

        return documentId +
                ":" +
                chunkIndex;
    }
}