package com.ai_document_knowledge_assistant.dto.responce;


import com.ai_document_knowledge_assistant.entity.enums.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO representing enrollment details.
 *
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {

    /** Enrollment identifier. */
    private Long id;

    /** Enrolled student identifier. */
    private Long studentId;

    /** Full student name. */
    private String studentName;

    /** Enrolled course identifier. */
    private Long courseId;

    /** Enrolled course code. */
    private String courseCode;

    /** Enrolled course name. */
    private String courseName;

    /** Enrollment date. */
    private LocalDate enrollmentDate;

    /** Enrollment status. */
    private EnrollmentStatus status;

    /** Record creation timestamp. */
    private LocalDateTime createdAt;

    /** Record update timestamp. */
    private LocalDateTime updatedAt;

}

