package com.ai_document_knowledge_assistant.mapper;



import com.ai_document_knowledge_assistant.dto.responce.ConversationDocumentResponse;
import com.ai_document_knowledge_assistant.model.entity.ConversationDocumentEntity;
import org.mapstruct.Mapper;

/**
 * Maps conversation document entities to responses.
 */
@Mapper(componentModel = "spring")
public interface ConversationDocumentMapper {

    /** Maps an entity to a response. */
    ConversationDocumentResponse toResponse(
            ConversationDocumentEntity entity
    );

    /** Creates an entity from conversation and document IDs. */
    ConversationDocumentEntity toEntity(
            String conversationId,
            String documentId
    );
}