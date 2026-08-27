package com.ai_document_knowledge_assistant.service.serviceImpl;

import com.ai_document_knowledge_assistant.model.VectorDocument;
import com.ai_document_knowledge_assistant.model.VectorSearchResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Selects the final document chunks that will be provided
 * to the Large Language Model (LLM).
 *
 * <p>
 * The selector is document-aware so that a single document
 * does not consume the entire context budget when multiple
 * documents are being searched.
 * </p>
 *
 * <p>
 * Semantic results receive priority over neighboring chunks.
 * The final selection is constrained by:
 * </p>
 *
 * <ul>
 *     <li>maximum number of chunks</li>
 *     <li>maximum number of characters</li>
 *     <li>per-document chunk allocation</li>
 * </ul>
 */
@Service
public class ContextSelector {

    @Value("${app.rag.context.max-chunks:8}")
    private int maxContextChunks;

    @Value("${app.rag.context.max-characters:5000}")
    private int maxContextCharacters;

    /**
     * Selects the final chunks that will be provided to the LLM.
     *
     * <p>
     * Selection happens in two phases:
     * </p>
     *
     * <ol>
     *     <li>Select the most relevant semantic chunks.</li>
     *     <li>Use the remaining context budget for neighboring chunks.</li>
     * </ol>
     *
     * <p>
     * Both phases respect the per-document allocation,
     * maximum chunk count, and maximum character count.
     * </p>
     *
     * @param semanticResults directly matched semantic results
     * @param expandedResults semantic results including neighboring chunks
     * @return final chunks selected for the LLM context
     */
    public List<VectorSearchResult> select(
            final List<VectorSearchResult> semanticResults,
            final List<VectorSearchResult> expandedResults
    ) {

        validateInputs(
                semanticResults,
                expandedResults
        );

        /*
         * Determine which documents actually produced
         * semantic results.
         */
        final List<String> documentIds =
                extractDocumentIds(
                        semanticResults
                );

        if (documentIds.isEmpty()) {
            return List.of();
        }

        /*
         * Calculate how many chunks each document
         * is allowed to contribute.
         */
        final Map<String, Integer> documentQuotas =
                calculateDocumentQuotas(
                        documentIds
                );

        /*
         * Track how many chunks have already been
         * selected from each document.
         */
        final Map<String, Integer> documentCounts =
                initializeDocumentCounts(
                        documentIds
                );

        /*
         * Stores the final selected chunks.
         *
         * LinkedHashMap gives us:
         *
         * documentId + chunkIndex
         *              ↓
         * unique chunk
         */
        final Map<String, VectorSearchResult> selected =
                new LinkedHashMap<>();

        int characterCount = 0;

        /*
         * =========================================================
         * PHASE 1
         * Semantic results
         * =========================================================
         *
         * Semantic results have highest priority because they
         * directly matched the user's question.
         */
        characterCount =
                selectSemanticResults(
                        semanticResults,
                        selected,
                        documentCounts,
                        documentQuotas,
                        characterCount
                );

        /*
         * =========================================================
         * PHASE 2
         * Neighbor results
         * =========================================================
         *
         * Use remaining context capacity for neighboring chunks
         * to preserve surrounding context.
         */
        characterCount =
                selectNeighborResults(
                        expandedResults,
                        selected,
                        documentCounts,
                        documentQuotas,
                        characterCount
                );

        /*
         * =========================================================
         * PHASE 3
         * Restore document order
         * =========================================================
         *
         * Retrieval ranking is used during selection.
         *
         * However, the LLM should receive chunks in their natural
         * document order so that surrounding context makes sense.
         */
        return sortByDocumentOrder(
                selected
        );
    }

    /**
     * Selects semantic search results while respecting:
     *
     * <ul>
     *     <li>global chunk limit</li>
     *     <li>global character limit</li>
     *     <li>per-document quota</li>
     *     <li>duplicate prevention</li>
     * </ul>
     *
     * @return updated character count
     */
    private int selectSemanticResults(
            final List<VectorSearchResult> semanticResults,
            final Map<String, VectorSearchResult> selected,
            final Map<String, Integer> documentCounts,
            final Map<String, Integer> documentQuotas,
            int characterCount
    ) {

        for (VectorSearchResult result : semanticResults) {

            /*
             * Global chunk limit reached.
             */
            if (selected.size() >= maxContextChunks) {
                break;
            }

            final VectorDocument document =
                    result.document();

            final String documentId =
                    document.documentId();

            final int currentCount =
                    documentCounts.getOrDefault(
                            documentId,
                            0
                    );

            final int quota =
                    documentQuotas.getOrDefault(
                            documentId,
                            0
                    );

            /*
             * Document has already consumed its allocation.
             */
            if (currentCount >= quota) {
                continue;
            }

            final String key =
                    createKey(
                            document.documentId(),
                            document.chunkIndex()
                    );

            /*
             * Prevent duplicate chunks.
             */
            if (selected.containsKey(key)) {
                continue;
            }

            final int length =
                    getContentLength(
                            document
                    );

            /*
             * Respect character budget.
             */
            if (!fitsCharacterBudget(
                    characterCount,
                    length
            )) {
                continue;
            }

            /*
             * Select semantic result.
             */
            selected.put(
                    key,
                    result
            );

            documentCounts.put(
                    documentId,
                    currentCount + 1
            );

            characterCount += length;
        }

        return characterCount;
    }

    /**
     * Selects neighboring chunks while respecting:
     *
     * <ul>
     *     <li>global chunk limit</li>
     *     <li>global character limit</li>
     *     <li>per-document quota</li>
     *     <li>duplicate prevention</li>
     * </ul>
     *
     * @return updated character count
     */
    private int selectNeighborResults(
            final List<VectorSearchResult> expandedResults,
            final Map<String, VectorSearchResult> selected,
            final Map<String, Integer> documentCounts,
            final Map<String, Integer> documentQuotas,
            int characterCount
    ) {

        for (VectorSearchResult result : expandedResults) {

            /*
             * Global chunk limit reached.
             */
            if (selected.size() >= maxContextChunks) {
                break;
            }

            final VectorDocument document =
                    result.document();

            final String documentId =
                    document.documentId();

            final int currentCount =
                    documentCounts.getOrDefault(
                            documentId,
                            0
                    );

            final int quota =
                    documentQuotas.getOrDefault(
                            documentId,
                            0
                    );

            /*
             * Document has already consumed its allocation.
             */
            if (currentCount >= quota) {
                continue;
            }

            final String key =
                    createKey(
                            document.documentId(),
                            document.chunkIndex()
                    );

            /*
             * Skip chunks that were already selected
             * during semantic selection.
             */
            if (selected.containsKey(key)) {
                continue;
            }

            final int length =
                    getContentLength(
                            document
                    );

            /*
             * Respect character budget.
             */
            if (!fitsCharacterBudget(
                    characterCount,
                    length
            )) {
                continue;
            }

            /*
             * Select neighboring chunk.
             */
            selected.put(
                    key,
                    result
            );

            documentCounts.put(
                    documentId,
                    currentCount + 1
            );

            characterCount += length;
        }

        return characterCount;
    }

    /**
     * Extracts the unique document IDs that actually produced
     * semantic results.
     *
     * <p>
     * We intentionally use semantic results rather than the
     * originally requested document IDs. A requested document
     * may not have any matching chunks.
     * </p>
     */
    private List<String> extractDocumentIds(
            final List<VectorSearchResult> semanticResults
    ) {

        return semanticResults.stream()
                .map(result ->
                        result.document()
                                .documentId()
                )
                .distinct()
                .toList();
    }

    /**
     * Calculates the maximum number of chunks that each document
     * can contribute.
     *
     * <p>
     * The allocation is distributed as evenly as possible.
     * </p>
     *
     * <p>
     * Example:
     *
     * <pre>
     * maxContextChunks = 8
     * documents = 3
     *
     * PDF-A → 3
     * PDF-B → 3
     * PDF-C → 2
     * </pre>
     * </p>
     */
    private Map<String, Integer> calculateDocumentQuotas(
            final List<String> documentIds
    ) {

        final int documentCount =
                documentIds.size();

        final int baseQuota =
                maxContextChunks / documentCount;

        final int remainder =
                maxContextChunks % documentCount;

        final Map<String, Integer> quotas =
                new LinkedHashMap<>();

        for (int i = 0; i < documentIds.size(); i++) {

            final String documentId =
                    documentIds.get(i);

            final int quota =
                    baseQuota
                            + (i < remainder ? 1 : 0);

            quotas.put(
                    documentId,
                    quota
            );
        }

        return quotas;
    }

    /**
     * Initializes the number of selected chunks for each document.
     */
    private Map<String, Integer> initializeDocumentCounts(
            final List<String> documentIds
    ) {

        final Map<String, Integer> counts =
                new LinkedHashMap<>();

        for (String documentId : documentIds) {

            counts.put(
                    documentId,
                    0
            );
        }

        return counts;
    }

    /**
     * Checks whether another chunk can fit inside
     * the configured character budget.
     */
    private boolean fitsCharacterBudget(
            final int currentCharacterCount,
            final int chunkLength
    ) {

        return currentCharacterCount + chunkLength
                <= maxContextCharacters;
    }

    /**
     * Returns the content length of a document chunk.
     *
     * <p>
     * Null content is treated as zero length to keep
     * context selection defensive.
     * </p>
     */
    private int getContentLength(
            final VectorDocument document
    ) {

        if (document.content() == null) {
            return 0;
        }

        return document.content().length();
    }

    /**
     * Creates a unique key for a document chunk.
     *
     * <p>
     * A chunk is uniquely identified by:
     *
     * <pre>
     * documentId + chunkIndex
     * </pre>
     */
    private String createKey(
            final String documentId,
            final int chunkIndex
    ) {

        return documentId + ":" + chunkIndex;
    }

    /**
     * Restores natural document/chunk order before
     * returning the final context.
     */
    private List<VectorSearchResult> sortByDocumentOrder(
            final Map<String, VectorSearchResult> selected
    ) {

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

    /**
     * Validates selector input.
     */
    private void validateInputs(
            final List<VectorSearchResult> semanticResults,
            final List<VectorSearchResult> expandedResults
    ) {

        if (semanticResults == null) {
            throw new IllegalArgumentException(
                    "Semantic results cannot be null"
            );
        }

        if (expandedResults == null) {
            throw new IllegalArgumentException(
                    "Expanded results cannot be null"
            );
        }

        if (maxContextChunks <= 0) {
            throw new IllegalStateException(
                    "maxContextChunks must be greater than zero"
            );
        }

        if (maxContextCharacters <= 0) {
            throw new IllegalStateException(
                    "maxContextCharacters must be greater than zero"
            );
        }
    }
}