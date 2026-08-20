package com.ai_document_knowledge_assistant.service.validation;


import com.ai_document_knowledge_assistant.dto.request.EnrollmentRequest;
import com.ai_document_knowledge_assistant.entity.Enrollment;

/**
 * Service responsible for validating enrollment related business rules.
 *
 */
public interface EnrollmentValidationService {

    /**
     * Validates an enrollment request.
     *
     * @param enrollmentRequest enrollment request
     *
     */
    void validateEnrollmentRequest(EnrollmentRequest enrollmentRequest);

    /**
     * Retrieves an enrollment by its identifier.
     *
     * @param enrollmentId enrollment identifier
     * @return enrollment entity
     *
     */
    Enrollment getEnrollmentByIdOrThrow(Long enrollmentId);

}
