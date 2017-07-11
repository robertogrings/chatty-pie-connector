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

package com.chattypie.service.chattypie;

import static java.lang.String.format;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import com.chattypie.service.chattypie.account.ChattyPieAccount;
import com.chattypie.service.chattypie.account.ChattyPieAccountService;
import com.chattypie.util.Delayer;

@RunWith(MockitoJUnitRunner.class)
public class ChattyPieAccountServiceTest {

	private ChattyPieAccountService testedService;

	@Mock
	private RestTemplate mockRestTemplate;
	@Mock
	private Delayer delayer;
	private String mockChattyPieHost = "http://www.example.com";

	@Before
	public void setUp() throws Exception {
		testedService = new ChattyPieAccountService(mockRestTemplate, mockChattyPieHost, delayer);
	}

	@Test
	public void testCreateChattyPieAccount_whenRestCallToChattyPieSucceeds_thenTheResponseOfTheRestCallIsPassedToCaller() throws Exception {
		//Given
		int expectedMaxRooms = 100;
		ChattyPieAccount expectedChattyPieAccount = new ChattyPieAccount("someId", expectedMaxRooms);
		when(
				mockRestTemplate.postForObject(
						format("%s/accounts", mockChattyPieHost),
						format("{\"max_allowed_rooms\": %d}", expectedMaxRooms),
						ChattyPieAccount.class
				)
		).thenReturn(expectedChattyPieAccount);

		//When
		ChattyPieAccount chattyPieAccountCreated = testedService.createChattyPieAccount();

		//Then
		assertThat(chattyPieAccountCreated).isEqualTo(expectedChattyPieAccount);
		verify(delayer).delayFor(ofSeconds(5));
	}
}
