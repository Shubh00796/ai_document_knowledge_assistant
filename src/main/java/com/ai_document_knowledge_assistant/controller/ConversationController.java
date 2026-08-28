package com.ai_document_knowledge_assistant.controller;

import com.ai_document_knowledge_assistant.dto.responce.ConversationMessageResponse;
import com.ai_document_knowledge_assistant.dto.responce.ConversationResponse;
import com.ai_document_knowledge_assistant.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @PostMapping
    public ResponseEntity<ConversationResponse> createConversation() {

        return ResponseEntity.ok(
                conversationService.createConversation()
        );
    }

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<List<ConversationMessageResponse>> getMessages(
            @PathVariable String conversationId
    ) {

        return ResponseEntity.ok(
                conversationService.getMessages(conversationId)
        );
    }
}