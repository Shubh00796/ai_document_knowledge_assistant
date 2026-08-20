package com.ai_document_knowledge_assistant.controller;


import com.ai_document_knowledge_assistant.dto.request.CourseRequest;
import com.ai_document_knowledge_assistant.dto.responce.CourseResponse;
import com.ai_document_knowledge_assistant.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing course resources.
 *
 */
@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /**
     * Creates a new course.
     *
     * @param courseRequest course details
     * @return created course
     *
     */
    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(
            @Valid @RequestBody CourseRequest courseRequest) {

        CourseResponse courseResponse =
                courseService.createCourse(courseRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseResponse);
    }

    /**
     * Retrieves a course by its identifier.
     *
     * @param courseId course identifier
     * @return course details
     *
     */
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponse> getCourseById(
            @PathVariable Long courseId) {

        return ResponseEntity.ok(
                courseService.getCourseById(courseId));
    }

    /**
     * Retrieves all available courses.
     *
     * @return list of courses
     *
     */
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {

        return ResponseEntity.ok(
                courseService.getAllCourses());
    }

    /**
     * Updates an existing course.
     *
     * @param courseId course identifier
     * @param courseRequest updated course details
     * @return updated course
     *
     */
    @PutMapping("/{courseId}")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseRequest courseRequest) {

        return ResponseEntity.ok(
                courseService.updateCourse(courseId, courseRequest));
    }

    /**
     * Deletes a course.
     *
     * @param courseId course identifier
     * @return no content
     *
     */
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long courseId) {

        courseService.deleteCourse(courseId);

        return ResponseEntity.noContent().build();
    }
}
