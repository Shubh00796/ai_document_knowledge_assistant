package com.ai_document_knowledge_assistant.mapper;


import com.ai_document_knowledge_assistant.dto.responce.EnrollmentResponse;
import com.ai_document_knowledge_assistant.entity.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting Enrollment entity into response DTO.
 *
 */
@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    /**
     * Converts Enrollment entity to EnrollmentResponse.
     *
     * @param enrollment enrollment entity
     * @return enrollment response
     *
     */
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName",
            expression = "java(enrollment.getStudent().getFirstName() + \" \" + enrollment.getStudent().getLastName())")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseCode", source = "course.courseCode")
    @Mapping(target = "courseName", source = "course.courseName")
    EnrollmentResponse toResponse(Enrollment enrollment);

}
