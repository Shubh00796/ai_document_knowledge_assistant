package com.ai_document_knowledge_assistant.service;



import com.ai_document_knowledge_assistant.dto.request.StudentRequest;
import com.ai_document_knowledge_assistant.dto.responce.StudentResponse;

import java.util.List;

/**
 * Service interface for managing students.
 *
 */
public interface StudentService {

    /**
     * Creates a new student.
     *
     * @param studentRequest request object
     * @return created student
     *
     */
    StudentResponse createStudent(StudentRequest studentRequest);

    /**
     * Retrieves a student by id.
     *
     * @param studentId student id
     * @return student details
     *
     */
    StudentResponse getStudentById(Long studentId);

    /**
     * Retrieves all students.
     *
     * @return list of students
     *
     */
    List<StudentResponse> getAllStudents();

    /**
     * Updates an existing student.
     *
     * @param studentId student id
     * @param studentRequest updated details
     * @return updated student
     *
     */
    StudentResponse updateStudent(Long studentId, StudentRequest studentRequest);

    /**
     * Deletes a student.
     *
     * @param studentId student id
     *
     */
    void deleteStudent(Long studentId);




}

