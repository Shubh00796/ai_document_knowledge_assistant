package com.ai_document_knowledge_assistant.repository;


import com.ai_document_knowledge_assistant.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Course entity.
 *
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Checks whether a course exists with the given course code.
     *
     * @param courseCode course code
     * @return true if course exists
     *
     */
    boolean existsByCourseCode(String courseCode);

    /**
     * Finds a course using the course code.
     *
     * @param courseCode course code
     * @return optional course
     *
     */
    Optional<Course> findByCourseCode(String courseCode);
}
