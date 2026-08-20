package com.ai_document_knowledge_assistant.service.impl;


import com.ai_document_knowledge_assistant.dto.request.StudentRequest;
import com.ai_document_knowledge_assistant.dto.responce.StudentResponse;
import com.ai_document_knowledge_assistant.entity.Student;
import com.ai_document_knowledge_assistant.mapper.StudentMapper;
import com.ai_document_knowledge_assistant.repository.StudentRepository;
import com.ai_document_knowledge_assistant.service.StudentService;
import com.ai_document_knowledge_assistant.service.validation.StudentValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default implementation of StudentService.
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final StudentValidationService studentValidationService;

    @Override
    @Transactional
    public StudentResponse createStudent(StudentRequest studentRequest) {

        log.info("Creating student with email : {}", studentRequest.getEmail());

        studentValidationService.validateStudentForCreation(
                studentRequest.getEmail(),
                studentRequest.getMobileNumber());

        Student student = studentMapper.toEntity(studentRequest);

        Student savedStudent = studentRepository.save(student);

        log.info("Student created successfully with id : {}", savedStudent.getId());

        return studentMapper.toResponse(savedStudent);
    }

    @Override
    public StudentResponse getStudentById(Long studentId) {

        log.info("Fetching student with id : {}", studentId);

        Student student = studentValidationService.validateAndGetStudent(studentId);

        return studentMapper.toResponse(student);
    }

    @Override
    public List<StudentResponse> getAllStudents() {

        log.info("Fetching all students");

        return studentRepository.findAll()
                .stream()
                .map(studentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(Long studentId,
                                         StudentRequest studentRequest) {

        log.info("Updating student with id : {}", studentId);

        Student student =
                studentValidationService.validateAndGetStudent(studentId);

        studentValidationService.validateStudentForUpdation(
                student,
                studentRequest.getEmail(),
                studentRequest.getMobileNumber());

        studentMapper.updateStudentFromRequest(studentRequest, student);

        Student updatedStudent = studentRepository.save(student);

        log.info("Student updated successfully with id : {}", updatedStudent.getId());

        return studentMapper.toResponse(updatedStudent);
    }

    @Override
    @Transactional
    public void deleteStudent(Long studentId) {

        log.info("Deleting student with id: {}", studentId);

        Student student =
                studentValidationService.validateAndGetStudent(studentId);

        studentValidationService.validateStudentDeletion(studentId);

        studentRepository.delete(student);

        log.info("Student deleted successfully with id: {}", studentId);
    }
}
