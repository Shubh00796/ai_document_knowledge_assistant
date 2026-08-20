package com.ai_document_knowledge_assistant.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Represents a course that students can enroll in.
 *
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "courses")
public class Course {

    /** Course primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;

    /** Unique course code. */
    @Column(name = "course_code", nullable = false, unique = true, length = 20)
    private String courseCode;

















    /** Course display name. */
    @Column(name = "course_name", nullable = false, length = 100)
    private String courseName;

    /** Optional course description. */
    @Column(name = "description", length = 500)
    private String description;

    /** Number of credits assigned to the course. */
    @Column(name = "credits", nullable = false)
    private Integer credits;

    /** Timestamp when the record was created. */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp when the record was last updated. */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
