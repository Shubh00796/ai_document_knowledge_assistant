package com.ai_document_knowledge_assistant.service.validation;


import com.ai_document_knowledge_assistant.dto.request.CourseRequest;
import com.ai_document_knowledge_assistant.entity.Course;

/**
 * Service responsible for validating course-related business rules.
 *
 */
public interface CourseValidationService {

    /**
     * Validates a course creation request.
     *
     * @param courseRequest request object
     *
     */
    void validateCourseCreationRequest(CourseRequest courseRequest);

    /**
     * Retrieves a course by id or throws an exception if not found.
     *
     * @param courseId course id
     * @return course entity
     *
     */
    Course getCourseByIdOrThrow(Long courseId);

    /**
     * Validates a course update request.
     *
     * @param existingCourse existing course
     * @param courseRequest updated request
     *
     */
    void validateCourseUpdateRequest(Course existingCourse,
                                     CourseRequest courseRequest);
}
