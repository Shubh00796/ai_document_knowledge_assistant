package com.ai_document_knowledge_assistant.service.impl;


import com.ai_document_knowledge_assistant.dto.request.CourseRequest;
import com.ai_document_knowledge_assistant.dto.responce.CourseResponse;
import com.ai_document_knowledge_assistant.entity.Course;
import com.ai_document_knowledge_assistant.mapper.CourseMapper;
import com.ai_document_knowledge_assistant.repository.CourseRepository;
import com.ai_document_knowledge_assistant.service.CourseService;
import com.ai_document_knowledge_assistant.service.validation.CourseValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default implementation of CourseService.
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final CourseValidationService courseValidationService;

    @Override
    @Transactional
    public CourseResponse createCourse(CourseRequest courseRequest) {

        log.info("Creating course with code: {}", courseRequest.getCourseCode());

        courseValidationService.validateCourseCreationRequest(courseRequest);

        Course course = courseMapper.toEntity(courseRequest);

        Course savedCourse = courseRepository.save(course);

        log.info("Course created successfully with id: {}", savedCourse.getId());

        return courseMapper.toResponse(savedCourse);
    }

    @Override
    public CourseResponse getCourseById(Long courseId) {

        log.info("Fetching course with id: {}", courseId);

        Course course = courseValidationService.getCourseByIdOrThrow(courseId);

        return courseMapper.toResponse(course);
    }

    @Override
    public List<CourseResponse> getAllCourses() {

        log.info("Fetching all courses.");

        return courseRepository.findAll()
                .stream()
                .map(courseMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(Long courseId,
                                       CourseRequest courseRequest) {

        log.info("Updating course with id: {}", courseId);

        Course existingCourse =
                courseValidationService.getCourseByIdOrThrow(courseId);

        courseValidationService.validateCourseUpdateRequest(
                existingCourse,
                courseRequest);

        courseMapper.updateCourseFromRequest(courseRequest, existingCourse);

        Course updatedCourse = courseRepository.save(existingCourse);

        log.info("Course updated successfully with id: {}", updatedCourse.getId());

        return courseMapper.toResponse(updatedCourse);
    }

    @Override
    @Transactional
    public void deleteCourse(Long courseId) {

        log.info("Deleting course with id: {}", courseId);

        Course course =
                courseValidationService.getCourseByIdOrThrow(courseId);

        courseRepository.delete(course);

        log.info("Course deleted successfully with id: {}", courseId);
    }
}
