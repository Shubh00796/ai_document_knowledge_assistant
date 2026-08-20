package com.ai_document_knowledge_assistant.client;

import com.ai_document_knowledge_assistant.dto.request.OllamaGenerateRequest;
import com.ai_document_knowledge_assistant.dto.responce.OllamaGenerateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class OllamaClient {

    private static final String GENERATE_ENDPOINT = "/api/generate";

    private final RestClient ollamaRestClient;

    public OllamaGenerateResponse generate(
            final OllamaGenerateRequest request
    ) {
        long start = System.currentTimeMillis();

        log.debug(
                "Calling Ollama generate endpoint with model '{}' and prompt length {}",
                request.model(),
                request.prompt() == null ? 0 : request.prompt().length()
        );

        OllamaGenerateResponse response = ollamaRestClient
                .post()
                .uri(GENERATE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(OllamaGenerateResponse.class);

        long elapsed = System.currentTimeMillis() - start;

        log.debug("Ollama generate call completed in {} ms", elapsed);

        return response;
    }
}