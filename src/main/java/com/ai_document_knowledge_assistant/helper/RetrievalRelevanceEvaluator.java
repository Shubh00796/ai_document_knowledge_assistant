package com.ai_document_knowledge_assistant.helper;

import com.ai_document_knowledge_assistant.model.VectorSearchResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RetrievalRelevanceEvaluator {

    @Value("${app.rag.retrieval.min-similarity:0.55}")
    private double minSimilarity;

    /**
     * Determines whether the provided semantic search results contain at least
     * one item whose similarity score meets the configured minimum threshold.
     *
     * @param semanticResults the semantic retrieval results to evaluate
     * @return {@code true} if the highest similarity score is greater than or
     * equal to the configured minimum similarity; otherwise {@code false}
     */
    public boolean isRelevant(
            List<VectorSearchResult> semanticResults
    ) {

        if (validateSemanticResult(semanticResults)) return false;

        return semanticResults.stream()
                .mapToDouble(VectorSearchResult::similarity)
                .max()
                .orElse(0.0)
                >= minSimilarity;
    }

    /**
     * Validates whether the semantic result collection is absent or empty.
     *
     * @param semanticResults the semantic retrieval results to validate
     * @return {@code true} if the input is {@code null} or empty; otherwise {@code false}
     */
    private static boolean validateSemanticResult(List<VectorSearchResult> semanticResults) {
        return semanticResults == null || semanticResults.isEmpty();
    }
}