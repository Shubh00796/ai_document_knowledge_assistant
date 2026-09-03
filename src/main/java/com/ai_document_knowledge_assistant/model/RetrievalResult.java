package com.ai_document_knowledge_assistant.model;

import java.util.List;

/**
 * The result of a retrieval step.
 */
public record RetrievalResult(
        List<VectorSearchResult> results,
        boolean relevant
) {
}
