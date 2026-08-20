package com.ai_document_knowledge_assistant.controller;

import com.ai_document_knowledge_assistant.dto.request.RagRequest;
import com.ai_document_knowledge_assistant.dto.responce.RagResponse;
import com.ai_document_knowledge_assistant.service.serviceImpl.RagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
public class RagController {

    private final RagService ragService;

    @PostMapping("/ask")
    public ResponseEntity<RagResponse> ask(
            @Valid @RequestBody RagRequest request
    ) {

        RagResponse response =
                ragService.answer(
                        request.question()
                );

        return ResponseEntity.ok(response);
    }
}