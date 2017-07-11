/*
 * Copyright 2017 AppDirect, Inc. and/or its affiliates
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
