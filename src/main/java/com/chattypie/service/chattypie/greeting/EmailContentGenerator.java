package com.chattypie.service.chattypie.greeting;

import static com.chattypie.util.ResourceUtils.readResourceFileAsString;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import com.chattypie.persistence.model.ChatroomCreationRecord;
import com.google.common.collect.ImmutableMap;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

@Slf4j
public class EmailContentGenerator {

	String generateNewCompanyGreeting(String companyName) {
		String template = readResourceFileAsString("templates/NewCompanyGreetingTemplate.html");
		Template messageTemplate = Mustache.compiler().compile(template);
		Map<String, String> params = ImmutableMap.<String, String>builder()
			.put("companyName", companyName)
			.build();
		return messageTemplate.execute(params);
	}

	String generateCreatedChatroomsReport(ZonedDateTime reportGenerationDate, List<ChatroomCreationRecord> chatrooms) {
		String template = readResourceFileAsString("templates/ChatroomsCreatedReport.html");
		Template messageTemplate = Mustache.compiler().compile(template);
		Map<String, Object> params = ImmutableMap.<String, Object>builder()
			.put("chatrooms", chatrooms)
			.put("reportGenerationDate", reportGenerationDate)
			.build();
		return messageTemplate.execute(params);
	}
}
