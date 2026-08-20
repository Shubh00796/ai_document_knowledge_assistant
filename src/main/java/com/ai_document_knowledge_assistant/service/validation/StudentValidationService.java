package com.ai_document_knowledge_assistant.service.validation;


import com.ai_document_knowledge_assistant.entity.Student;

/**
 * Service responsible for validating student-related business rules.
 *
 */
public interface StudentValidationService {

    /**
     * Validates student uniqueness before creation.
     *
     * @param email student email
     * @param mobileNumber student mobile number
     *
     */
    void validateStudentForCreation(String email, String mobileNumber);

    /**
     * Retrieves a student by id or throws an exception when absent.
     *
     * @param studentId student identifier
     * @return student entity
     *
     */
    Student validateAndGetStudent(Long studentId);

    /**
     * Validates student uniqueness before updating.
     *
     * @param student existing student entity
     * @param email requested email
     * @param mobileNumber requested mobile number
     *
     */
    void validateStudentForUpdation(Student student,
                                    String email,
                                    String mobileNumber);

    /**
     * Validates whether the student can be deleted.
     *
     * @param studentId student identifier
     *
     */
    void validateStudentDeletion(Long studentId);
}
