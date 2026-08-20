package com.ai_document_knowledge_assistant.service.serviceImpl;

import com.ai_document_knowledge_assistant.model.Embedding;
import com.ai_document_knowledge_assistant.model.VectorDocument;
import com.ai_document_knowledge_assistant.model.VectorSearchResult;
import com.ai_document_knowledge_assistant.service.EmbeddingService;
import com.ai_document_knowledge_assistant.service.RetrievalService;
import com.ai_document_knowledge_assistant.service.VectorStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Retrieves relevant document chunks using semantic vector search
 * and expands the results with neighboring chunks.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RetrievalServiceImpl implements RetrievalService {

    private final EmbeddingService embeddingService;
    private final VectorStore vectorStore;

    @Value("${app.rag.retrieval.neighbor-radius:1}")
    private int neighborRadius;

    @Value("${app.rag.context.max-chunks:8}")
    private int maxContextChunks;

    @Value("${app.rag.context.max-characters:5000}")
    private int maxContextCharacters;

    /**
     * Retrieves document chunks relevant to the given question.
     *
     * <p>The question is converted into an embedding, searched against
     * the vector store, and the matching chunks are expanded with their
     * neighboring chunks to preserve surrounding context.</p>
     *
     * @param question user's question
     * @param topK maximum number of semantic results to retrieve
     * @return selected document chunks relevant to the question
     * @throws IllegalArgumentException if the question is blank or topK is invalid
     */
    @Override
    public List<VectorSearchResult> retrieve(
            final String question,
            final int topK
    ) {

        validateQuestion(question);
        validateTopK(topK);

        final long start = System.currentTimeMillis();

        /*
         * 1. Question → embedding
         */
        final Embedding queryEmbedding =
                embeddingService.embed(question);

        /*
         * 2. Semantic vector search
         */
        final List<VectorSearchResult> semanticResults =
                vectorStore.search(
                        queryEmbedding.vector(),
                        topK
                );

        /*
         * 3. Expand semantic results with
         *    neighboring chunks.
         */
        final List<VectorSearchResult> expandedResults =
                expandWithNeighbors(
                        semanticResults
                );

        final List<VectorSearchResult> selectedResults =
                selectContext(
                        semanticResults,
                        expandedResults
                );

        final long elapsed =
                System.currentTimeMillis() - start;

        printRetrievalDebug(
                question,
                topK,
                semanticResults,
                expandedResults,
                elapsed
        );

        return selectedResults;
    }

    /**
     * Adds neighboring chunks around each semantic search result.
     *
     * <p>The original semantic results retain their similarity scores,
     * while neighboring chunks are added with a neutral score.</p>
     *
     * @param semanticResults results returned by semantic search
     * @return semantic results combined with their neighboring chunks
     */
    private List<VectorSearchResult> expandWithNeighbors(
            final List<VectorSearchResult> semanticResults
    ) {

        final Map<String, VectorSearchResult> expanded =
                new LinkedHashMap<>();

        for (VectorSearchResult result : semanticResults) {

            final VectorDocument document =
                    result.document();

            /*
             * Keep the original semantic result
             * with its real similarity score.
             */
            expanded.put(
                    createKey(
                            document.documentId(),
                            document.chunkIndex()
                    ),
                    result
            );

            /*
             * Find previous + current + next chunk
             * directly from the in-memory vector store.
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

                /*
                 * Do not overwrite a semantic result.
                 */
                expanded.putIfAbsent(
                        key,
                        new VectorSearchResult(
                                neighbor,
                                0.0
                        )
                );
            }
        }

        /*
         * Arrange chunks in document order.
         */
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
                .toList();
    }

    /**
     * Logs retrieval information useful for debugging and performance analysis.
     *
     * @param question user's question
     * @param topK requested number of semantic results
     * @param semanticResults original semantic search results
     * @param expandedResults results including neighboring chunks
     * @param elapsed retrieval execution time in milliseconds
     */
    private void printRetrievalDebug(
            final String question,
            final int topK,
            final List<VectorSearchResult> semanticResults,
            final List<VectorSearchResult> expandedResults,
            final long elapsed
    ) {

        log.debug(
                """
                ========== RETRIEVAL SERVICE ==========
                Question: {}
                Top-K: {}
                Semantic results: {}
                Expanded results: {}
                Time: {} ms
                =======================================
                """,
                question,
                topK,
                semanticResults.size(),
                expandedResults.size(),
                elapsed
        );

        log.debug("------ SEMANTIC RESULTS ------");

        for (VectorSearchResult result : semanticResults) {

            log.debug(
                    "Chunk index: {}, similarity: {}",
                    result.document().chunkIndex(),
                    result.similarity()
            );
        }

        log.debug("------ EXPANDED RESULTS ------");

        for (VectorSearchResult result : expandedResults) {

            log.debug(
                    "Chunk index: {}, similarity: {}",
                    result.document().chunkIndex(),
                    result.similarity()
            );
        }
    }

    /**
     * Creates a unique key for a document chunk.
     *
     * @param documentId document identifier
     * @param chunkIndex chunk index within the document
     * @return unique document-chunk key
     */
    private String createKey(
            final String documentId,
            final int chunkIndex
    ) {

        return documentId + ":" + chunkIndex;
    }

    /**
     * Validates the user's question.
     *
     * @param question user's question
     * @throws IllegalArgumentException if the question is null or blank
     */
    private void validateQuestion(
            final String question
    ) {

        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException(
                    "Question cannot be blank"
            );
        }
    }

    /**
     * Validates the requested number of semantic results.
     *
     * @param topK number of results to retrieve
     * @throws IllegalArgumentException if topK is not positive
     */
    private void validateTopK(
            final int topK
    ) {

        if (topK <= 0) {
            throw new IllegalArgumentException(
                    "topK must be greater than zero"
            );
        }
    }

    /**
     * Selects the final chunks that will be provided to the LLM.
     *
     * <p>Semantic matches receive priority, followed by neighboring chunks.
     * The selection is limited by both chunk count and total character count.</p>
     *
     * @param semanticResults directly matched semantic results
     * @param expandedResults semantic results with neighboring chunks
     * @return final chunks selected for the LLM context
     */
    private List<VectorSearchResult> selectContext(
            final List<VectorSearchResult> semanticResults,
            final List<VectorSearchResult> expandedResults
    ) {

        final Map<String, VectorSearchResult> selected =
                new LinkedHashMap<>();

        int characterCount = 0;

        /*
         * First priority:
         * semantic search results.
         */
        for (VectorSearchResult result : semanticResults) {

            if (selected.size() >= maxContextChunks) {
                break;
            }

            final int length =
                    result.document()
                            .content()
                            .length();

            if (characterCount + length
                    > maxContextCharacters) {
                continue;
            }

            final String key =
                    createKey(
                            result.document().documentId(),
                            result.document().chunkIndex()
                    );

            selected.putIfAbsent(
                    key,
                    result
            );

            characterCount += length;
        }

        /*
         * Second priority:
         * neighboring chunks.
         */
        for (VectorSearchResult result : expandedResults) {

            if (selected.size() >= maxContextChunks) {
                break;
            }

            final String key =
                    createKey(
                            result.document().documentId(),
                            result.document().chunkIndex()
                    );

            if (selected.containsKey(key)) {
                continue;
            }

            final int length =
                    result.document()
                            .content()
                            .length();

            if (characterCount + length
                    > maxContextCharacters) {
                continue;
            }

            selected.put(
                    key,
                    result
            );

            characterCount += length;
        }

        /*
         * Restore document order before sending
         * the context to the LLM.
         */
        return selected.values()
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
                .toList();
    }
}