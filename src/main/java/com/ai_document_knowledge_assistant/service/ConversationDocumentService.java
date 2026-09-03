package com.ai_document_knowledge_assistant.service;


import com.ai_document_knowledge_assistant.dto.request.AttachDocumentRequest;
import com.ai_document_knowledge_assistant.dto.responce.ConversationDocumentResponse;

import java.util.List;

/**
 * Manages documents linked to conversations.
 */
public interface ConversationDocumentService {

    /** Returns documents for a conversation. */
    List<ConversationDocumentResponse> getDocuments(
            String conversationId
    );

    /** Attaches a document to a conversation. */
    ConversationDocumentResponse attachDocument(
            String conversationId,
            AttachDocumentRequest request
    );

    /** Detaches a document from a conversation. */
    void detachDocument(
            String conversationId,
            String documentId
    );
}