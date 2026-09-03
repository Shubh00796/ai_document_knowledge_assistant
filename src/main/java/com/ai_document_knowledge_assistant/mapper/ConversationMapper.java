package com.ai_document_knowledge_assistant.mapper;

import com.ai_document_knowledge_assistant.dto.responce.ConversationMessageResponse;
import com.ai_document_knowledge_assistant.dto.responce.ConversationResponse;
import com.ai_document_knowledge_assistant.model.entity.ConversationEntity;
import com.ai_document_knowledge_assistant.model.entity.ConversationMessageEntity;
import org.mapstruct.Mapper;

/**
 * Maps conversation entities to response objects.
 */
@Mapper(componentModel = "spring")
public interface ConversationMapper {

    /** Maps a conversation entity to a response. */
    ConversationResponse toResponse(ConversationEntity entity);

    /** Maps a message entity to a response. */
    ConversationMessageResponse toResponse(ConversationMessageEntity entity);
}