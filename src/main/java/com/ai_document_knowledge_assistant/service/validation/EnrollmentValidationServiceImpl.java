package com.ai_document_knowledge_assistant.service.validation;


import com.ai_document_knowledge_assistant.dto.request.EnrollmentRequest;
import com.ai_document_knowledge_assistant.entity.Course;
import com.ai_document_knowledge_assistant.entity.Enrollment;
import com.ai_document_knowledge_assistant.entity.Student;
import com.ai_document_knowledge_assistant.exception.AlreadyEnrolledException;
import com.ai_document_knowledge_assistant.exception.ResourceNotFoundException;
import com.ai_document_knowledge_assistant.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Default implementation of EnrollmentValidationService.
 *
 */
@Service
@RequiredArgsConstructor
public class EnrollmentValidationServiceImpl implements EnrollmentValidationService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentValidationService studentValidationService;
    private final CourseValidationService courseValidationService;

    @Override
    public void validateEnrollmentRequest(EnrollmentRequest enrollmentRequest) {

        Student student = studentValidationService.validateAndGetStudent(
                enrollmentRequest.getStudentId());

        Course course = courseValidationService.getCourseByIdOrThrow(
                enrollmentRequest.getCourseId());

        validateDuplicateEnrollment(student.getId(), course.getId());
    }

    @Override
    public Enrollment getEnrollmentByIdOrThrow(Long enrollmentId) {

        return enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Enrollment not found with id: " + enrollmentId));
    }

    /**
     * Validates whether a student is already enrolled in the given course.
     *
     * @param studentId student identifier
     * @param courseId course identifier
     */
    private void validateDuplicateEnrollment(Long studentId,
                                             Long courseId) {

        if (enrollmentRepository.existsByStudent_IdAndCourse_Id(studentId, courseId)) {

            throw new AlreadyEnrolledException(
                    "Student is already enrolled in the selected course.");
        }
    }

}
