package com.ai_document_knowledge_assistant.mapper;


import com.ai_document_knowledge_assistant.dto.request.CourseRequest;
import com.ai_document_knowledge_assistant.dto.responce.CourseResponse;
import com.ai_document_knowledge_assistant.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Mapper for converting between Course entity and DTOs.
 *
 */
@Mapper(componentModel = "spring")
public interface CourseMapper {

    /**
     * Converts CourseRequest to Course entity.
     *
     * @param courseRequest request object
     * @return Course entity
     *
     */
    Course toEntity(CourseRequest courseRequest);

    /**
     * Converts Course entity to CourseResponse.
     *
     * @param course course entity
     * @return Course response
     *
     */
    CourseResponse toResponse(Course course);

    /**
     * Updates an existing Course entity using the provided request.
     *
     * @param courseRequest request object
     * @param course existing course entity
     *
     */
    void updateCourseFromRequest(CourseRequest courseRequest,
                                 @MappingTarget Course course);
}
