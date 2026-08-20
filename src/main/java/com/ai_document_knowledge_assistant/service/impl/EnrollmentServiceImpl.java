package com.ai_document_knowledge_assistant.service.impl;


import com.ai_document_knowledge_assistant.dto.request.EnrollmentRequest;
import com.ai_document_knowledge_assistant.dto.responce.EnrollmentResponse;
import com.ai_document_knowledge_assistant.entity.Course;
import com.ai_document_knowledge_assistant.entity.Enrollment;
import com.ai_document_knowledge_assistant.entity.Student;
import com.ai_document_knowledge_assistant.entity.enums.EnrollmentStatus;
import com.ai_document_knowledge_assistant.mapper.EnrollmentMapper;
import com.ai_document_knowledge_assistant.repository.EnrollmentRepository;
import com.ai_document_knowledge_assistant.service.EnrollmentService;
import com.ai_document_knowledge_assistant.service.validation.CourseValidationService;
import com.ai_document_knowledge_assistant.service.validation.EnrollmentValidationService;
import com.ai_document_knowledge_assistant.service.validation.StudentValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Default implementation of EnrollmentService.
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentMapper enrollmentMapper;
    private final EnrollmentValidationService enrollmentValidationService;
    private final StudentValidationService studentValidationService;
    private final CourseValidationService courseValidationService;

    @Override
    @Transactional
    public EnrollmentResponse enrollStudent(EnrollmentRequest enrollmentRequest) {

        log.info("Processing enrollment request for studentId: {} and courseId: {}",
                enrollmentRequest.getStudentId(),
                enrollmentRequest.getCourseId());

        enrollmentValidationService.validateEnrollmentRequest(enrollmentRequest);

        Student student = studentValidationService.validateAndGetStudent(
                enrollmentRequest.getStudentId());

        Course course = courseValidationService.getCourseByIdOrThrow(
                enrollmentRequest.getCourseId());


        Enrollment enrollment = buildEnrollment(student, course);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        log.info("Enrollment created successfully with id: {}",
                savedEnrollment.getId());

        return enrollmentMapper.toResponse(savedEnrollment);
    }

    @Override
    public EnrollmentResponse getEnrollmentById(Long enrollmentId) {

        log.info("Fetching enrollment with id: {}", enrollmentId);

        Enrollment enrollment =
                enrollmentValidationService.getEnrollmentByIdOrThrow(enrollmentId);

        return enrollmentMapper.toResponse(enrollment);
    }

    @Override
    public List<EnrollmentResponse> getAllEnrollments() {

        log.info("Fetching all enrollments.");

        return enrollmentRepository.findAll()
                .stream()
                .map(enrollmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteEnrollment(Long enrollmentId) {

        log.info("Deleting enrollment with id: {}", enrollmentId);

        Enrollment enrollment =
                enrollmentValidationService.getEnrollmentByIdOrThrow(enrollmentId);

        enrollmentRepository.delete(enrollment);

        log.info("Enrollment deleted successfully with id: {}",
                enrollmentId);
    }

    /**
     * Creates an Enrollment entity from the supplied Student and Course.
     *
     * @param student student entity
     * @param course course entity
     * @return enrollment entity
     *
     */
    private Enrollment buildEnrollment(Student student,
                                       Course course) {

        return Enrollment.builder()
                .student(student)
                .course(course)
                .enrollmentDate(LocalDate.now())
                .status(EnrollmentStatus.ACTIVE)
                .build();
    }
}
