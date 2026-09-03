package com.ai_document_knowledge_assistant.mapper;



import com.ai_document_knowledge_assistant.dto.responce.ConversationDocumentResponse;
import com.ai_document_knowledge_assistant.model.entity.ConversationDocumentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConversationDocumentMapper {

    ConversationDocumentResponse toResponse(
            ConversationDocumentEntity entity
    );

    ConversationDocumentEntity toEntity(
            String conversationId,
            String documentId
    );
}