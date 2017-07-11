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

import static com.chattypie.service.chattypie.greeting.EmailNotificationService.CHATROOMS_CREATED_REPORT_SUBJECT;
import static com.chattypie.service.chattypie.greeting.EmailNotificationService.WELCOME_EMAIL_SUBJECT;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.appdirect.sdk.appmarket.events.CompanyInfo;
import com.appdirect.sdk.notification.HtmlEmailNotificationService;
import com.chattypie.persistence.model.ChatroomCreationRecord;
import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class EmailNotificationServiceTest {

	private EmailNotificationService testedGreetingService;

	@Mock
	private HtmlEmailNotificationService mockEmailNotificationService;

	@Mock
	private EmailContentGenerator mockContentGenerator;

	@Before
	public void setUp() throws Exception {
		testedGreetingService = new EmailNotificationService(mockContentGenerator, mockEmailNotificationService);
	}

	@Test
	public void testSendDailyChatroomCreatedReport_whenCalledWithATargetAddressAndAListOfCreatedChatrooms_reportIsgeneratedAndSent() throws Exception {
		//Given
		String testSubscriberEmail = "test@example.com";
		ChatroomCreationRecord chatroom1 = new ChatroomCreationRecord(Instant.EPOCH, "chatroom1");
		ChatroomCreationRecord chatroom2 = new ChatroomCreationRecord(Instant.EPOCH.plusSeconds(1), "chatroom2");
		ChatroomCreationRecord chatroom3 = new ChatroomCreationRecord(Instant.EPOCH.plusSeconds(2), "chatroom3");

		List<ChatroomCreationRecord> expectedChatrooms = Lists.newArrayList(chatroom1, chatroom2, chatroom3);

		String expectedReportBody = "expectedReportBody";

		when(mockContentGenerator.generateCreatedChatroomsReport(any(), eq(expectedChatrooms)))
			.thenReturn(expectedReportBody);

		//When
		testedGreetingService.sendDailyChatroomCreatedReport(testSubscriberEmail, expectedChatrooms);

		//Then
		verify(mockEmailNotificationService)
			.sendHtmlEmail(
				eq(CHATROOMS_CREATED_REPORT_SUBJECT),
				eq(expectedReportBody),
				eq(testSubscriberEmail)
			);
	}

	@Test
	public void sendNewCompanyGreeting_whenSendIsSuccessful_theGreetingIsSentAsEmail() throws Exception {
		//Given
		String expectedGreetingDestinationEmail = "newCompany@example.com";
		CompanyInfo testCompany = CompanyInfo.builder()
			.email(expectedGreetingDestinationEmail)
			.build();
		String expectedEmailContent = "Expected Email Content";
		when(mockContentGenerator.generateNewCompanyGreeting(anyString()))
			.thenReturn(expectedEmailContent);

		//When
		testedGreetingService.sendNewCompanyGreeting(testCompany);

		//Then
		verify(mockEmailNotificationService).sendHtmlEmail(
			WELCOME_EMAIL_SUBJECT,
			expectedEmailContent,
			testCompany.getEmail()
		);
	}
}
