package com.ai_document_knowledge_assistant.service.serviceImpl;

import com.ai_document_knowledge_assistant.model.VectorSearchResult;
import com.ai_document_knowledge_assistant.service.VectorStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalancedSemanticRetriever {

    private final VectorStore vectorStore;

    public List<VectorSearchResult> retrieve(
            List<String> documentIds,
            List<Float> queryVector,
            int topK
    ) {

        final int documentCount =
                documentIds.size();

        final int baseK =
                topK / documentCount;

        final int remainder =
                topK % documentCount;

        if (topK < documentCount) {

            log.debug(
                    "topK ({}) is smaller than document count ({}). " +
                            "Only {} documents can contribute semantic results.",
                    topK,
                    documentCount,
                    topK
            );
        }

        final List<VectorSearchResult> candidates =
                new ArrayList<>();

        for (int i = 0; i < documentIds.size(); i++) {

            final String documentId =
                    documentIds.get(i);

            final int perDocumentK =
                    baseK + (i < remainder ? 1 : 0);

            if (perDocumentK <= 0) {
                continue;
            }

            final List<VectorSearchResult> results =
                    vectorStore.search(
                            List.of(documentId),
                            queryVector,
                            perDocumentK
                    );

            candidates.addAll(results);
        }

        return candidates.stream()
                .sorted(
                        Comparator.comparingDouble(
                                VectorSearchResult::similarity
                        ).reversed()
                )
                .toList();
    }
}