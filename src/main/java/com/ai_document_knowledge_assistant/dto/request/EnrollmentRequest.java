package com.ai_document_knowledge_assistant.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO for enrolling a student into a course.
 *
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequest {

    /** Identifier of the student to enroll. */
    @NotNull(message = "Student id is required.")
    private Long studentId;

    /** Identifier of the course to enroll in. */
    @NotNull(message = "Course id is required.")
    private Long courseId;

}

