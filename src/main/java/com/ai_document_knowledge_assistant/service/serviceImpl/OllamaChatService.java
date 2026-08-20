package com.ai_document_knowledge_assistant.service.serviceImpl;

import com.ai_document_knowledge_assistant.client.OllamaClient;
import com.ai_document_knowledge_assistant.dto.request.OllamaGenerateRequest;
import com.ai_document_knowledge_assistant.dto.responce.OllamaGenerateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OllamaChatService {

    private final OllamaClient ollamaClient;

    @Value("${app.ollama.chat-model}")
    private String chatModel;

    public String generate(final String prompt) {
        final OllamaGenerateRequest request = new OllamaGenerateRequest(
                chatModel,
                prompt,
                false
        );

        final OllamaGenerateResponse response =
                ollamaClient.generate(request);

        logOllamaMetrics(response);


        return extractResponse(response);
    }

    private String extractResponse(
            final OllamaGenerateResponse response
    ) {
        validateOllamaResponse(response);

        return response.response();
    }

    private static void validateOllamaResponse(OllamaGenerateResponse response) {
        if (response == null || response.response() == null) {
            throw new IllegalStateException(
                    "Ollama returned an empty response"
            );
        }
    }


    private void logOllamaMetrics(
            final OllamaGenerateResponse response
    ) {

        if (response == null) {
            return;
        }

        log.debug(
                "Ollama metrics - total: {} ms, load: {} ms, promptTokens: {}, promptEval: {} ms, generatedTokens: {}, generation: {} ms",
                nanosToMillis(response.totalDuration()),
                nanosToMillis(response.loadDuration()),
                response.promptEvalCount(),
                nanosToMillis(response.promptEvalDuration()),
                response.evalCount(),
                nanosToMillis(response.evalDuration())
        );
    }

    private long nanosToMillis(final Long nanos) {
        if (nanos == null) {
            return 0;
        }

        return nanos / 1_000_000;
    }
}