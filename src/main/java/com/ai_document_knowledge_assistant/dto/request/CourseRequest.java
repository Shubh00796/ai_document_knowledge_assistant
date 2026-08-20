package com.ai_document_knowledge_assistant.dto.request;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO for creating or updating a course.
 *
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {

    /** Unique course code. */
    @NotBlank(message = "Course code is required.")
    @Size(max = 20, message = "Course code must not exceed 20 characters.")
    private String courseCode;

    /** Course display name. */
    @NotBlank(message = "Course name is required.")
    @Size(max = 100, message = "Course name must not exceed 100 characters.")
    private String courseName;

    /** Optional course description. */
    @Size(max = 500, message = "Description must not exceed 500 characters.")
    private String description;

    /** Credit value for the course. */
    @Min(value = 1, message = "Credits must be at least 1.")
    @Max(value = 10, message = "Credits must not exceed 10.")
    private Integer credits;
}
