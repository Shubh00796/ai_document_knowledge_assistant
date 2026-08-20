package com.ai_document_knowledge_assistant.service;



import com.ai_document_knowledge_assistant.dto.request.CourseRequest;
import com.ai_document_knowledge_assistant.dto.responce.CourseResponse;

import java.util.List;

/**
 * Service interface for managing courses.
 *
 */
public interface CourseService {

    /**
     * Creates a new course.
     *
     * @param courseRequest request object
     * @return created course
     *
     */
    CourseResponse createCourse(CourseRequest courseRequest);

    /**
     * Retrieves a course by id.
     *
     * @param courseId course id
     * @return course details
     *
     */
    CourseResponse getCourseById(Long courseId);

    /**
     * Retrieves all available courses.
     *
     * @return list of courses
     *
     */
    List<CourseResponse> getAllCourses();

    /**
     * Updates an existing course.
     *
     * @param courseId course id
     * @param courseRequest updated course details
     * @return updated course
     *
     */
    CourseResponse updateCourse(Long courseId,
                                CourseRequest courseRequest);

    /**
     * Deletes a course.
     *
     * @param courseId course id
     *
     */
    void deleteCourse(Long courseId);
}
