package com.ai_document_knowledge_assistant.service.validation;


import com.ai_document_knowledge_assistant.dto.request.CourseRequest;
import com.ai_document_knowledge_assistant.entity.Course;
import com.ai_document_knowledge_assistant.exception.DuplicateResourceException;
import com.ai_document_knowledge_assistant.exception.ResourceNotFoundException;
import com.ai_document_knowledge_assistant.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Default implementation of course validation service.
 *
 */
@Service
@RequiredArgsConstructor
public class CourseValidationServiceImpl implements CourseValidationService {

    private final CourseRepository courseRepository;

    @Override
    public void validateCourseCreationRequest(CourseRequest courseRequest) {

        if (courseRepository.existsByCourseCode(courseRequest.getCourseCode())) {
            throw new DuplicateResourceException(
                    "Course already exists with course code: "
                            + courseRequest.getCourseCode());
        }
    }

    @Override
    public Course getCourseByIdOrThrow(Long courseId) {

        return courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found with id: " + courseId));
    }

    @Override
    public void validateCourseUpdateRequest(Course existingCourse,
                                            CourseRequest courseRequest) {

        if (!existingCourse.getCourseCode().equals(courseRequest.getCourseCode())
                && courseRepository.existsByCourseCode(courseRequest.getCourseCode())) {

            throw new DuplicateResourceException(
                    "Course already exists with course code: "
                            + courseRequest.getCourseCode());
        }
    }
}
