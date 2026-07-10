/*
 * Application entry point.
 * Boots Spring Boot with embedded Tomcat and all components.
 */
package com.russtudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RusStudyApplication {

	public static void main(String[] args) {
		SpringApplication.run(RusStudyApplication.class, args);
	}
}