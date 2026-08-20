package com.ai_document_knowledge_assistant.service.serviceImpl;

import com.ai_document_knowledge_assistant.config.EmbeddingProperties;
import com.ai_document_knowledge_assistant.model.Embedding;
import com.ai_document_knowledge_assistant.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class OllamaEmbeddingService implements EmbeddingService {

    private final RestClient restClient;
    private final EmbeddingProperties properties;

    public OllamaEmbeddingService(
            RestClient.Builder restClientBuilder,
            EmbeddingProperties properties
    ) {
        this.properties = properties;
        this.restClient = restClientBuilder
                .baseUrl(properties.baseUrl())
                .build();
    }

    @Override
    public Embedding embed(String text) {
        validateText(text);

        OllamaEmbeddingRequest  request = new OllamaEmbeddingRequest(
                properties.model(),
                text
        );

        OllamaEmbeddingResponse response =
                restClient.post()
                        .uri("/api/embed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(request)
                        .retrieve()
                        .body(OllamaEmbeddingResponse.class);

        validateResponse(response);

        return new Embedding(response.embeddings().get(0));
    }

    private void validateText(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text cannot be blank");
        }
    }

    private void validateResponse(OllamaEmbeddingResponse response) {
        if (response == null
                || response.embeddings() == null
                || response.embeddings().isEmpty()) {

            throw new IllegalStateException(
                    "Ollama returned an empty embedding"
            );
        }
    }

    private record OllamaEmbeddingRequest(
            String model,
            String input
    ) {
    }

    private record OllamaEmbeddingResponse(
            String model,
            List<List<Float>> embeddings
    ) {
    }
}