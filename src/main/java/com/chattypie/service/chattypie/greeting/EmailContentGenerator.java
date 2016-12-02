package com.chattypie.service.chattypie.greeting;

import static com.google.common.io.Resources.getResource;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

import com.google.common.io.Resources;

@Slf4j
public class EmailContentGenerator {

	String generateWelcomeEmail(String companyName) {
		try {
			String template = Resources.toString(
				getResource("templates/NewCompanyGreetingTemplate.html"),
				UTF_8
			);
			return template.replace("$companyName$", companyName);
		} catch (IOException e) {
			log.error("Failed generating email content", e);
			throw new RuntimeException(e);
		}
	}
}
