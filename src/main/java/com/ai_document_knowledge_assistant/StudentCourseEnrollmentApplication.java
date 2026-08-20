package com.ai_document_knowledge_assistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Bootstraps the student course enrollment application.
 *
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class StudentCourseEnrollmentApplication {

	/**
	 * Starts the Spring Boot application.
	 *
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(StudentCourseEnrollmentApplication.class, args);
	}

}

