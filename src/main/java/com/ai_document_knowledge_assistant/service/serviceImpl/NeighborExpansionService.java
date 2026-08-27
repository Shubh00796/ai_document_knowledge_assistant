package com.ai_document_knowledge_assistant.service.serviceImpl;

import com.ai_document_knowledge_assistant.model.VectorDocument;
import com.ai_document_knowledge_assistant.model.VectorSearchResult;
import com.ai_document_knowledge_assistant.service.VectorStore;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Expands semantic search results by including neighboring chunks from the same
 * document within a configurable radius.
 */
@Service
@RequiredArgsConstructor
public class NeighborExpansionService {

    private final VectorStore vectorStore;

    @Value("${app.rag.retrieval.neighbor-radius:1}")
    private int neighborRadius;

    /**
     * Expands the provided semantic search results with neighboring document chunks.
     * <p>
     * Original semantic matches are preserved, while missing neighbor chunks are added
     * with a score of {@code 0.0}. The final result is de-duplicated and ordered by
     * document id and chunk index.
     * </p>
     *
     * @param semanticResults the semantic search results to expand
     * @return a sorted list containing semantic matches and their neighboring chunks
     */
    public List<VectorSearchResult> expand(
            List<VectorSearchResult> semanticResults
    ) {

        final Map<String, VectorSearchResult> expanded =
                new LinkedHashMap<>();

        for (VectorSearchResult result : semanticResults) {

            final VectorDocument document =
                    result.document();

            /*
             * Keep semantic result.
             */
            expanded.put(
                    createKey(
                            document.documentId(),
                            document.chunkIndex()
                    ),
                    result
            );

            /*
             * Add neighboring chunks.
             */
            final List<VectorDocument> neighbors =
                    vectorStore.findNeighbors(
                            document.documentId(),
                            document.chunkIndex(),
                            neighborRadius
                    );

            for (VectorDocument neighbor : neighbors) {

                final String key =
                        createKey(
                                neighbor.documentId(),
                                neighbor.chunkIndex()
                        );

                expanded.putIfAbsent(
                        key,
                        new VectorSearchResult(
                                neighbor.documentId(),
                                neighbor,
                                0.0
                        )
                );
            }
        }

        return expanded.values()
                .stream()
                .sorted(
                        Comparator
                                .comparing(
                                        (VectorSearchResult result) ->
                                                result.document()
                                                        .documentId()
                                )
                                .thenComparing(
                                        result ->
                                                result.document()
                                                        .chunkIndex()
                                )
                )
                .collect(Collectors.toList());
    }

    /**
     * Creates a unique key for a document chunk.
     *
     * @param documentId the document identifier
     * @param chunkIndex the chunk index within the document
     * @return a composite key in the format {@code documentId:chunkIndex}
     */
    private String createKey(
            String documentId,
            int chunkIndex
    ) {

        return documentId + ":" + chunkIndex;
    }
}