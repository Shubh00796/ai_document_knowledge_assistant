package com.ai_document_knowledge_assistant.service;



import com.ai_document_knowledge_assistant.dto.request.EnrollmentRequest;
import com.ai_document_knowledge_assistant.dto.responce.EnrollmentResponse;

import java.util.List;

/**
 * Service interface for managing enrollments.
 *
 */
public interface EnrollmentService {

    /**
     * Enrolls a student into a course.
     *
     * @param enrollmentRequest enrollment request
     * @return created enrollment
     *
     */
    EnrollmentResponse enrollStudent(EnrollmentRequest enrollmentRequest);

    /**
     * Retrieves an enrollment by identifier.
     *
     * @param enrollmentId enrollment identifier
     * @return enrollment details
     *
     */
    EnrollmentResponse getEnrollmentById(Long enrollmentId);

    /**
     * Retrieves all enrollments.
     *
     * @return list of enrollments
     *
     */
    List<EnrollmentResponse> getAllEnrollments();

    /**
     * Deletes an enrollment.
     *
     * @param enrollmentId enrollment identifier
     *
     */
    void deleteEnrollment(Long enrollmentId);

}

