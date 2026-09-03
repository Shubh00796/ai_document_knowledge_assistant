package com.ai_document_knowledge_assistant.controller;


import com.ai_document_knowledge_assistant.dto.request.AttachDocumentRequest;
import com.ai_document_knowledge_assistant.dto.responce.ConversationDocumentResponse;
import com.ai_document_knowledge_assistant.service.ConversationDocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationDocumentController {

    private final ConversationDocumentService conversationDocumentService;

    @GetMapping("/{conversationId}/documents")
    public ResponseEntity<List<ConversationDocumentResponse>> getDocuments(
            @PathVariable String conversationId
    ) {
        return ResponseEntity.ok(
                conversationDocumentService.getDocuments(conversationId)
        );
    }

    @PostMapping("/{conversationId}/documents")
    public ResponseEntity<ConversationDocumentResponse> attachDocument(
            @PathVariable String conversationId,
            @Valid @RequestBody AttachDocumentRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        conversationDocumentService.attachDocument(
                                conversationId,
                                request
                        )
                );
    }

    @DeleteMapping("/{conversationId}/documents/{documentId}")
    public ResponseEntity<Void> detachDocument(
            @PathVariable String conversationId,
            @PathVariable String documentId
    ) {
        conversationDocumentService.detachDocument(
                conversationId,
                documentId
        );

        return ResponseEntity.noContent().build();
    }
}
