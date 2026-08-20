package com.ai_document_knowledge_assistant.controller;


import com.ai_document_knowledge_assistant.dto.request.StudentRequest;
import com.ai_document_knowledge_assistant.dto.responce.StudentResponse;
import com.ai_document_knowledge_assistant.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing students.
 *
 */
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    /**
     * Creates a new student.
     *
     * @param studentRequest student details to persist
     * @return the created student
     *
     */
    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(
            @Valid @RequestBody StudentRequest studentRequest) {

        StudentResponse studentResponse =
                studentService.createStudent(studentRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(studentResponse);
    }

    /**
     * Retrieves a student by its identifier.
     *
     * @param studentId student identifier
     * @return the matching student
     *
     */
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentResponse> getStudentById(
            @PathVariable Long studentId) {

        return ResponseEntity.ok(
                studentService.getStudentById(studentId));
    }

    /**
     * Retrieves all students.
     *
     * @return the list of students
     *
     */
    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAllStudents() {

        return ResponseEntity.ok(
                studentService.getAllStudents());
    }

    /**
     * Updates an existing student.
     *
     * @param studentId student identifier
     * @param studentRequest updated student details
     * @return the updated student
     *
     */
    @PutMapping("/{studentId}")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable Long studentId,
            @Valid @RequestBody StudentRequest studentRequest) {

        return ResponseEntity.ok(
                studentService.updateStudent(studentId, studentRequest));
    }

    /**
     * Deletes a student.
     *
     * @param studentId student identifier
     * @return no content response
     *
     */
    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable Long studentId) {

        studentService.deleteStudent(studentId);

        return ResponseEntity.noContent().build();
    }
}
