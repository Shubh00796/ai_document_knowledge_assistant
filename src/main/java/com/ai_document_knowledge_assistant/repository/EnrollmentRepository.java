package com.ai_document_knowledge_assistant.repository;


import com.ai_document_knowledge_assistant.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Enrollment entity.
 *
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /**
     * Checks whether a student is already enrolled in a course.
     *
     * @param studentId student identifier
     * @param courseId course identifier
     * @return true if enrollment exists
     *
     */
    boolean existsByStudent_IdAndCourse_Id(Long studentId,
                                         Long courseId);

    /**
     * Retrieves all enrollments for a student.
     *
     * @param studentId student identifier
     * @return list of enrollments
     *
     */
    List<Enrollment> findByStudentId(Long studentId);

    /**
     * Retrieves all enrollments for a course.
     *
     * @param courseId course identifier
     * @return list of enrollments
     *
     */
    List<Enrollment> findByCourseId(Long courseId);

    /**
     * Checks whether any enrollment exists for a student.
     *
     * @param studentId student identifier
     * @return true if exists
     *
     */
    boolean existsByStudentId(Long studentId);

    /**
     * Checks whether any enrollment exists for a course.
     *
     * @param courseId course identifier
     * @return true if exists
     *
     */
    boolean existsByCourseId(Long courseId);

}

