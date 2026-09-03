package com.ai_document_knowledge_assistant.service.serviceImpl;


import com.ai_document_knowledge_assistant.dto.request.AttachDocumentRequest;
import com.ai_document_knowledge_assistant.dto.responce.ConversationDocumentResponse;
import com.ai_document_knowledge_assistant.mapper.ConversationDocumentMapper;
import com.ai_document_knowledge_assistant.model.entity.ConversationDocumentEntity;
import com.ai_document_knowledge_assistant.reposiotry_ai.ConversationDocumentRepository;
import com.ai_document_knowledge_assistant.service.ConversationDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ConversationDocumentServiceImpl
        implements ConversationDocumentService {

    private final ConversationDocumentRepository conversationDocumentRepository;
    private final ConversationDocumentMapper conversationDocumentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ConversationDocumentResponse> getDocuments(
            String conversationId
    ) {
        return conversationDocumentRepository
                .findByConversationId(conversationId)
                .stream()
                .map(conversationDocumentMapper::toResponse)
                .toList();
    }

    @Override
    public ConversationDocumentResponse attachDocument(
            String conversationId,
            AttachDocumentRequest request
    ) {
        String documentId = request.documentId();

        validateIfDocumentIsAlreadyAttachedToTheConversation(
                conversationId,
                documentId
        );

        ConversationDocumentEntity entity =
                conversationDocumentMapper.toEntity(
                        conversationId,
                        documentId
                );

        ConversationDocumentEntity savedEntity =
                conversationDocumentRepository.save(entity);

        return conversationDocumentMapper.toResponse(savedEntity);
    }


    @Override
    public void detachDocument(
            String conversationId,
            String documentId
    ) {
        conversationDocumentRepository
                .deleteByConversationIdAndDocumentId(
                        conversationId,
                        documentId
                );
    }


    private void validateIfDocumentIsAlreadyAttachedToTheConversation(String conversationId, String documentId) {
        if (conversationDocumentRepository
                .existsByConversationIdAndDocumentId(
                        conversationId,
                        documentId
                )) {

            throw new IllegalStateException(
                    "Document is already attached to this conversation"
            );
        }
    }
}