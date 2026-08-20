package com.ai_document_knowledge_assistant.controller;


import com.ai_document_knowledge_assistant.dto.request.EnrollmentRequest;
import com.ai_document_knowledge_assistant.dto.responce.EnrollmentResponse;
import com.ai_document_knowledge_assistant.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing student course enrollments.
 *
 */
@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    /**
     * Creates a new enrollment.
     *
     * @param enrollmentRequest enrollment request
     * @return created enrollment
     *
     */
    @PostMapping
    public ResponseEntity<EnrollmentResponse> createEnrollment(
            @Valid @RequestBody EnrollmentRequest enrollmentRequest) {

        EnrollmentResponse enrollmentResponse =
                enrollmentService.enrollStudent(enrollmentRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enrollmentResponse);
    }

    /**
     * Retrieves an enrollment by its identifier.
     *
     * @param enrollmentId enrollment identifier
     * @return enrollment details
     *
     */
    @GetMapping("/{enrollmentId}")
    public ResponseEntity<EnrollmentResponse> getEnrollmentById(
            @PathVariable Long enrollmentId) {

        EnrollmentResponse enrollmentResponse =
                enrollmentService.getEnrollmentById(enrollmentId);

        return ResponseEntity.ok(enrollmentResponse);
    }

    /**
     * Retrieves all enrollments.
     *
     * @return list of enrollments
     *
     */
    @GetMapping
    public ResponseEntity<List<EnrollmentResponse>> getAllEnrollments() {

        List<EnrollmentResponse> enrollmentResponses =
                enrollmentService.getAllEnrollments();

        return ResponseEntity.ok(enrollmentResponses);
    }

    /**
     * Deletes an enrollment.
     *
     * @param enrollmentId enrollment identifier
     * @return no content
     *
     */
    @DeleteMapping("/{enrollmentId}")
    public ResponseEntity<Void> deleteEnrollment(
            @PathVariable Long enrollmentId) {

        enrollmentService.deleteEnrollment(enrollmentId);

        return ResponseEntity.noContent().build();
    }

}
