package com.ai_document_knowledge_assistant.mapper;

import com.ai_document_knowledge_assistant.dto.request.StudentRequest;
import com.ai_document_knowledge_assistant.dto.responce.StudentResponse;
import com.ai_document_knowledge_assistant.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Mapper for converting between Student entity and DTOs.
 *
 */
@Mapper(componentModel = "spring")
public interface StudentMapper {

    /**
     * Converts StudentRequest to Student entity.
     *
     * @param studentRequest request object
     * @return Student entity
     *
     */
    Student toEntity(StudentRequest studentRequest);

    /**
     * Converts Student entity to StudentResponse.
     *
     * @param student student entity
     * @return Student response
     *
     */
    StudentResponse toResponse(Student student);

    /**
     * Updates an existing Student entity using StudentRequest.
     *
     * @param studentRequest request object
     * @param student existing student entity
     *
     */
    void updateStudentFromRequest(StudentRequest studentRequest,
                                  @MappingTarget Student student);
}
