package com.ai_document_knowledge_assistant.mapper;

import com.ai_document_knowledge_assistant.dto.responce.ConversationMessageResponse;
import com.ai_document_knowledge_assistant.dto.responce.ConversationResponse;
import com.ai_document_knowledge_assistant.model.entity.ConversationEntity;
import com.ai_document_knowledge_assistant.model.entity.ConversationMessageEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConversationMapper {

    ConversationResponse toResponse(ConversationEntity entity);

    ConversationMessageResponse toResponse(ConversationMessageEntity entity);
}