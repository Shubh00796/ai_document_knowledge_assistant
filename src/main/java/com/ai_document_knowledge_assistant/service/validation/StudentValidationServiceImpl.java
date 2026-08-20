package com.ai_document_knowledge_assistant.service.validation;


import com.ai_document_knowledge_assistant.entity.Student;
import com.ai_document_knowledge_assistant.exception.DuplicateResourceException;
import com.ai_document_knowledge_assistant.exception.ResourceNotFoundException;
import com.ai_document_knowledge_assistant.repository.EnrollmentRepository;
import com.ai_document_knowledge_assistant.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Default implementation of student validation rules.
 *
 */
@Service
@RequiredArgsConstructor
public class StudentValidationServiceImpl implements StudentValidationService {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public void validateStudentForCreation(String email, String mobileNumber) {
        validateEmailAvailability(email);
        validateMobileAvailability(mobileNumber);
    }

    @Override
    public Student validateAndGetStudent(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: " + studentId));
    }

    @Override
    public void validateStudentForUpdation(Student student,
                                           String email,
                                           String mobileNumber) {

        validateEmailAndMobile(student, email, mobileNumber);
    }

    @Override
    public void validateStudentDeletion(Long studentId) {

        if (enrollmentRepository.existsByStudentId(studentId)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Student cannot be deleted because enrollments exist.");
        }
    }



    private void validateEmailAndMobile(Student student, String email, String mobileNumber) {
        if (!student.getEmail().equals(email)) {
            validateEmailAvailability(email);
        }

        if (!student.getMobileNumber().equals(mobileNumber)) {
            validateMobileAvailability(mobileNumber);
        }
    }

    private void validateEmailAvailability(String email) {
        if (studentRepository.existsByEmail(email)) {
            throw new DuplicateResourceException(
                    "Student already exists with email: " + email);
        }
    }

    private void validateMobileAvailability(String mobileNumber) {
        if (studentRepository.existsByMobileNumber(mobileNumber)) {
            throw new DuplicateResourceException(
                    "Student already exists with mobile number: " + mobileNumber);
        }
    }
}
