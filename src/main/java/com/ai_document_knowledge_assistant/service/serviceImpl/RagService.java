package com.ai_document_knowledge_assistant.service.serviceImpl;

import com.ai_document_knowledge_assistant.dto.responce.RagResponse;
import com.ai_document_knowledge_assistant.dto.responce.RagSource;
import com.ai_document_knowledge_assistant.helper.RagPromptBuilder;
import com.ai_document_knowledge_assistant.model.VectorSearchResult;
import com.ai_document_knowledge_assistant.service.RetrievalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orchestrates the RAG flow:
 * retrieval → context → prompt → LLM response.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RagService {

    private static final String NO_CONTEXT_MESSAGE =
            "No relevant information was found in the provided documents.";

    private static final String INSUFFICIENT_CONTEXT_MESSAGE =
            "The provided documents do not contain enough information to answer this question.";

    private final RetrievalService retrievalService;
    private final RagPromptBuilder promptBuilder;
    private final OllamaChatService ollamaChatService;

    @Value("${app.rag.retrieval.top-k:5}")
    private int defaultTopK;

    /**
     * Answers a user question using retrieval + prompt building + LLM generation.
     */
    public RagResponse answer(final String documentId, final String question) {

        validateQuestion(question);
        validateDocumentId(documentId);

        final long totalStart =
                System.currentTimeMillis();

        /*
         * 1. Retrieve relevant document chunks.
         */
        final long retrievalStart =
                System.currentTimeMillis();

        final List<VectorSearchResult> results =
                retrievalService.retrieve(
                        documentId,
                        question,
                        defaultTopK
                );

        final long retrievalTime =
                System.currentTimeMillis() - retrievalStart;

        /*
         * 2. If nothing was retrieved,
         *    don't call the LLM.
         */
        if (results == null || results.isEmpty()) {

            log.info(
                    "No relevant document chunks found for question: {}",
                    question
            );

            return new RagResponse(
                    INSUFFICIENT_CONTEXT_MESSAGE,
                    List.of()
            );
        }

        /*
         * 3. Build document context.
         */
        final long contextStart =
                System.currentTimeMillis();

        final String context =
                buildContext(results);

        final long contextTime =
                System.currentTimeMillis() - contextStart;

        /*
         * 4. If retrieved chunks contain no usable text,
         *    don't call the LLM.
         */
        if (context.equals(NO_CONTEXT_MESSAGE)) {

            log.info(
                    "Retrieved chunks contained no usable document content"
            );

            return new RagResponse(
                    INSUFFICIENT_CONTEXT_MESSAGE,
                    List.of()
            );
        }

        /*
         * 5. Build RAG prompt.
         */
        final long promptStart =
                System.currentTimeMillis();

        final String prompt =
                promptBuilder.build(
                        question,
                        context
                );

        final long promptTime =
                System.currentTimeMillis() - promptStart;

        log.debug(
                "RAG context characters: {}",
                context.length()
        );

        log.debug(
                "RAG prompt characters: {}",
                prompt.length()
        );

        /*
         * 6. Send prompt to Ollama.
         */
        final long llmStart =
                System.currentTimeMillis();

        final String answer =
                ollamaChatService.generate(prompt);

        final long llmTime =
                System.currentTimeMillis() - llmStart;

        /*
         * 7. Total RAG timing.
         */
        final long totalTime =
                System.currentTimeMillis() - totalStart;

        log.info(
                "RAG timing -> retrieval: {} ms, context: {} ms, " +
                        "prompt: {} ms, llm: {} ms, total: {} ms",
                retrievalTime,
                contextTime,
                promptTime,
                llmTime,
                totalTime
        );

        return new RagResponse(
                answer,
                buildSources(results)
        );
    }

    /**
     * Builds a plain text context block from retrieved chunks.
     */
    private String buildContext(
            final List<VectorSearchResult> results
    ) {

        if (results == null || results.isEmpty()) {
            return NO_CONTEXT_MESSAGE;
        }

        StringBuilder context =
                new StringBuilder();

        boolean hasValidContext = false;

        context.append("DOCUMENT CONTEXT\n\n");

        for (VectorSearchResult result : results) {

            if (validateDocumentContent(result)) {
                continue;
            }

            hasValidContext = true;

            context.append("[Chunk ")
                    .append(
                            result.document()
                                    .chunkIndex()
                    )
                    .append("]\n");

            context.append(
                    result.document()
                            .content()
            );

            context.append("\n\n");
        }

        if (!hasValidContext) {
            return NO_CONTEXT_MESSAGE;
        }

        return context.toString();
    }

    /**
     * Checks whether a retrieved chunk has usable text content.
     */
    private static boolean validateDocumentContent(
            final VectorSearchResult result
    ) {

        return result == null
                || result.document() == null
                || result.document().content() == null
                || result.document().content().isBlank();
    }

    /**
     * Validates that the question is not null or blank.
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
     * Validates that the document ID is not null or blank.
     */
    private void validateDocumentId(
            final String documentId
    ) {

        if (documentId == null || documentId.isBlank()) {

            throw new IllegalArgumentException(
                    "Document ID cannot be blank"
            );
        }
    }

    /**
     * Maps valid retrieved chunks to lightweight source metadata.
     */
    private List<RagSource> buildSources(
            final List<VectorSearchResult> results
    ) {

        if (results == null || results.isEmpty()) {
            return List.of();
        }

        return results.stream()
                .filter(result -> !validateDocumentContent(result))
                .map(result -> new RagSource(
                        result.document().documentId(),
                        result.document().chunkIndex(),
                        result.similarity()
                ))
                .toList();
    }
}