package com.ai_document_knowledge_assistant.service.serviceImpl;

import com.ai_document_knowledge_assistant.helper.RetrievalRelevanceEvaluator;
import com.ai_document_knowledge_assistant.model.Embedding;
import com.ai_document_knowledge_assistant.model.VectorSearchResult;
import com.ai_document_knowledge_assistant.service.EmbeddingService;
import com.ai_document_knowledge_assistant.service.RetrievalService;
import com.ai_document_knowledge_assistant.service.validation.RetrievalValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orchestrates the document retrieval pipeline.
 *
 * <p>
 * The retrieval pipeline consists of:
 * </p>
 *
 * <ol>
 *     <li>Input validation</li>
 *     <li>Question embedding</li>
 *     <li>Balanced semantic retrieval</li>
 *     <li>Neighbor expansion</li>
 *     <li>Context selection</li>
 * </ol>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RetrievalServiceImpl implements RetrievalService {

    private final EmbeddingService embeddingService;
    private final BalancedSemanticRetriever balancedSemanticRetriever;
    private final NeighborExpansionService neighborExpansionService;
    private final ContextSelector contextSelector;
    private final RetrievalValidator retrievalValidator;
    private final RetrievalRelevanceEvaluator retrievalRelevanceEvaluator;


    @Override
    public List<VectorSearchResult> retrieve(
            List<String> documentIds,
            String question,
            int topK
    ) {

        retrievalValidator.validate(
                documentIds,
                question,
                topK
        );

        final long start =
                System.currentTimeMillis();

        /*
         * 1. Question → embedding
         */
        final Embedding queryEmbedding =
                embeddingService.embed(question);

        /*
         * 2. Balanced semantic retrieval
         */
        final List<VectorSearchResult> semanticResults =
                balancedSemanticRetriever.retrieve(
                        documentIds,
                        queryEmbedding.vector(),
                        topK
                );

        /*
         * 3. Relevance gate
         *
         * Determine whether the semantic search actually
         * found information relevant enough to continue
         * with the RAG pipeline.
         */
        final boolean relevant =
                retrievalRelevanceEvaluator.isRelevant(
                        semanticResults
                );

        List<VectorSearchResult> of = checkWeratherSearchFoundReleventResultOrNot(relevant);
        if (of != null) return of;
        /*
         * 3. Neighbor expansion
         */
        final List<VectorSearchResult> expandedResults =
                neighborExpansionService.expand(
                        semanticResults
                );

        /*
         * 4. Final context selection
         */
        final List<VectorSearchResult> selectedResults =
                contextSelector.select(
                        semanticResults,
                        expandedResults
                );

        logRetrieval(
                question,
                topK,
                semanticResults,
                expandedResults,
                selectedResults,
                System.currentTimeMillis() - start
        );

        return selectedResults;
    }

    private static @Nullable List<VectorSearchResult> checkWeratherSearchFoundReleventResultOrNot(boolean relevant) {
        if (!relevant) {

            log.debug(
                    "No sufficiently relevant chunks found. " +
                            "Skipping neighbor expansion and LLM context creation."
            );

            return List.of();
        }
        return null;
    }

    private void logRetrieval(
            String question,
            int topK,
            List<VectorSearchResult> semanticResults,
            List<VectorSearchResult> expandedResults,
            List<VectorSearchResult> selectedResults,
            long elapsed
    ) {

        log.debug(
                """
                ========== RETRIEVAL SERVICE ==========
                Question: {}
                Top-K: {}
                Semantic results: {}
                Expanded results: {}
                Final context: {}
                Time: {} ms
                ========================================
                """,
                question,
                topK,
                semanticResults.size(),
                expandedResults.size(),
                selectedResults.size(),
                elapsed
        );
    }
}