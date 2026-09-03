package com.ai_document_knowledge_assistant.service;


import com.ai_document_knowledge_assistant.dto.request.AttachDocumentRequest;
import com.ai_document_knowledge_assistant.dto.responce.ConversationDocumentResponse;

import java.util.List;

public interface ConversationDocumentService {

    List<ConversationDocumentResponse> getDocuments(
            String conversationId
    );

    ConversationDocumentResponse attachDocument(
            String conversationId,
            AttachDocumentRequest request
    );

    void detachDocument(
            String conversationId,
            String documentId
    );
}