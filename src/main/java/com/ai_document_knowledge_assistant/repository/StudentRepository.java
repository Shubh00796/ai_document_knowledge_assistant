package com.ai_document_knowledge_assistant.repository;


import com.ai_document_knowledge_assistant.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Student entity.
 *
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Checks whether a student exists with the given email.
     *
     * @param email student's email
     * @return true if exists
     *
     */
    boolean existsByEmail(String email);

    /**
     * Checks whether a student exists with the given mobile number.
     *
     * @param mobileNumber student's mobile number
     * @return true if exists
     *
     */
    boolean existsByMobileNumber(String mobileNumber);

    /**
     * Finds a student by email.
     *
     * @param email student's email
     * @return optional student
     *
     */
    Optional<Student> findByEmail(String email);
}

