package com.ai_document_knowledge_assistant.dto.responce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO representing student details.
 *
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {

    /** Student identifier. */
    private Long id;

    /** Student first name. */
    private String firstName;

    /** Student last name. */
    private String lastName;

    /** Student email address. */
    private String email;

    /** Student mobile number. */
    private String mobileNumber;

    /** Student date of birth. */
    private LocalDate dateOfBirth;

    /** Record creation timestamp. */
    private LocalDateTime createdAt;

    /** Record update timestamp. */
    private LocalDateTime updatedAt;
}
