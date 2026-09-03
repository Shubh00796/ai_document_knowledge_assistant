package com.ai_document_knowledge_assistant.service.serviceImpl;

import com.ai_document_knowledge_assistant.dto.request.MessageRequest;
import com.ai_document_knowledge_assistant.dto.responce.ConversationDocumentResponse;
import com.ai_document_knowledge_assistant.dto.responce.ConversationMessageResponse;
import com.ai_document_knowledge_assistant.dto.responce.RagResponse;
import com.ai_document_knowledge_assistant.dto.responce.RagSource;
import com.ai_document_knowledge_assistant.helper.RagPromptBuilder;
import com.ai_document_knowledge_assistant.model.VectorSearchResult;
import com.ai_document_knowledge_assistant.model.entity.ConversationDocumentEntity;
import com.ai_document_knowledge_assistant.reposiotry_ai.ConversationDocumentRepository;
import com.ai_document_knowledge_assistant.service.ConversationDocumentService;
import com.ai_document_knowledge_assistant.service.ConversationService;
import com.ai_document_knowledge_assistant.service.RetrievalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orchestrates the complete conversational RAG flow:
 *
 * validation
 *      ↓
 * conversation history
 *      ↓
 * attached conversation documents
 *      ↓
 * semantic retrieval
 *      ↓
 * relevance gate
 *      ↓
 * context creation
 *      ↓
 * prompt construction
 *      ↓
 * LLM generation
 *      ↓
 * persist user + assistant messages
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RagService {

    private static final String NO_CONTEXT_MESSAGE =
            "No relevant information was found in the provided documents.";

    private static final String INSUFFICIENT_CONTEXT_MESSAGE =
            "The provided documents do not contain enough information " +
                    "to answer this question.";

    private final RetrievalService retrievalService;
    private final RagPromptBuilder promptBuilder;
    private final OllamaChatService ollamaChatService;
    private final ConversationService conversationService;
    private final ConversationDocumentRepository conversationDocumentRepository;
   private final  ConversationDocumentService conversationDocumentService;

    @Value("${app.rag.retrieval.top-k:5}")
    private int defaultTopK;

    /**
     * Answers a question using conversational RAG.
     */
    public RagResponse answer(
            final String conversationId,
            final String question
    ) {

        validateConversationId(conversationId);
        validateQuestion(question);

        /*
         * 1. Find documents attached to this conversation.
         */
        final List<String> documentIds =
                conversationDocumentService
                        .getDocuments(conversationId)
                        .stream()
                        .map(ConversationDocumentResponse::documentId)
                        .toList();

        validateDocumentIds(documentIds);

        final long totalStart =
                System.currentTimeMillis();

        /*
         * 2. Load previous conversation history.
         */
        final long historyStart =
                System.currentTimeMillis();

        final List<ConversationMessageResponse> history =
                conversationService.getMessages(conversationId);

        final long historyTime =
                System.currentTimeMillis() - historyStart;

        log.debug(
                "Conversation history loaded - conversationId: {}, messages: {}, time: {} ms",
                conversationId,
                history.size(),
                historyTime
        );

        /*
         * 3. Retrieve relevant document chunks.
         */
        final long retrievalStart =
                System.currentTimeMillis();

        final List<VectorSearchResult> results =
                retrievalService.retrieve(
                        documentIds,
                        question,
                        defaultTopK
                );

        final long retrievalTime =
                System.currentTimeMillis() - retrievalStart;

        /*
         * 4. Relevance gate.
         *
         * If semantic retrieval found nothing relevant,
         * do NOT call the LLM.
         */
        if (results == null || results.isEmpty()) {

            log.info(
                    "No relevant document chunks found. " +
                            "Skipping LLM. conversationId={}, question={}",
                    conversationId,
                    question
            );

            return new RagResponse(
                    INSUFFICIENT_CONTEXT_MESSAGE,
                    List.of()
            );
        }

        /*
         * 5. Build document context.
         */
        final long contextStart =
                System.currentTimeMillis();

        final String context =
                buildContext(results);

        final long contextTime =
                System.currentTimeMillis() - contextStart;

        /*
         * 6. Verify context contains usable content.
         */
        if (context.equals(NO_CONTEXT_MESSAGE)) {

            log.info(
                    "Retrieved chunks contained no usable document content. " +
                            "conversationId={}",
                    conversationId
            );

            return new RagResponse(
                    INSUFFICIENT_CONTEXT_MESSAGE,
                    List.of()
            );
        }

        /*
         * 7. Build conversational RAG prompt.
         */
        final long promptStart =
                System.currentTimeMillis();

        final String prompt =
                promptBuilder.build(
                        history,
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
         * 8. Call LLM.
         */
        final long llmStart =
                System.currentTimeMillis();

        final String answer =
                ollamaChatService.generate(prompt);

        final long llmTime =
                System.currentTimeMillis() - llmStart;

        /*
         * 9. Persist user message.
         */
        conversationService.addUserMessage(
                conversationId,
                new MessageRequest(question)
        );

        /*
         * 10. Persist assistant message.
         */
        conversationService.addAssistantMessage(
                conversationId,
                new MessageRequest(answer)
        );

        /*
         * 11. Total timing.
         */
        final long totalTime =
                System.currentTimeMillis() - totalStart;

        log.info(
                "RAG timing -> history: {} ms, retrieval: {} ms, " +
                        "context: {} ms, prompt: {} ms, llm: {} ms, total: {} ms",
                historyTime,
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
     * Builds document context from retrieved chunks.
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

            context.append("[Document ")
                    .append(result.document().documentId())
                    .append(" | Chunk ")
                    .append(result.document().chunkIndex())
                    .append("]\n");

            context.append(
                    result.document().content()
            );

            context.append("\n\n");
        }

        if (!hasValidContext) {
            return NO_CONTEXT_MESSAGE;
        }

        return context.toString();
    }

    /**
     * Checks whether retrieved document content is usable.
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
     * Validates conversation ID.
     */
    private void validateConversationId(
            final String conversationId
    ) {

        if (conversationId == null ||
                conversationId.isBlank()) {

            throw new IllegalArgumentException(
                    "Conversation ID cannot be blank"
            );
        }
    }

    /**
     * Validates question.
     */
    private void validateQuestion(
            final String question
    ) {

        if (question == null ||
                question.isBlank()) {

            throw new IllegalArgumentException(
                    "Question cannot be blank"
            );
        }
    }

    /**
     * Validates document IDs.
     */
    private void validateDocumentIds(
            final List<String> documentIds
    ) {

        if (documentIds == null ||
                documentIds.isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one document must be attached to the conversation"
            );
        }

        if (documentIds.stream()
                .anyMatch(id -> id == null || id.isBlank())) {

            throw new IllegalArgumentException(
                    "Document ID cannot be blank"
            );
        }
    }

    /**
     * Maps retrieved chunks to source metadata.
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