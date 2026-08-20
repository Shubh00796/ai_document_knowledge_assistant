package com.ai_document_knowledge_assistant.dto.responce;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Response DTO representing course details.
 *
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {

    /** Course identifier. */
    private Long id;

    /** Unique course code. */
    private String courseCode;

    /** Course display name. */
    private String courseName;

    /** Optional course description. */
    private String description;

    /** Course credit value. */
    private Integer credits;

    /** Record creation timestamp. */
    private LocalDateTime createdAt;

    /** Record update timestamp. */
    private LocalDateTime updatedAt;
}

