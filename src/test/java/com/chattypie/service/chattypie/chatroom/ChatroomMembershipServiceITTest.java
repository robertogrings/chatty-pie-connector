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

package com.chattypie.service.chattypie.chatroom;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static java.lang.String.format;

import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(MockitoJUnitRunner.class)
public class ChatroomMembershipServiceITTest {

	private ChatroomMembershipService chatroomService;

	@Rule
	public WireMockRule mockServer = new WireMockRule(options().dynamicPort());

	@Before
	public void setUp() throws Exception {
		String testChattyPieHost = format("http://localhost:%d", mockServer.port());
		chatroomService = new ChatroomMembershipService(new RestTemplate(), testChattyPieHost);
	}

	@Test
	public void testAssignUser() throws Exception {
		//Given
		String testChatroomId = UUID.randomUUID().toString();
		String testUserEmail = "auser@example.com";
		final String expectedUrl = format(
				"/rooms/%s/users/%s",
				testChatroomId,
				testUserEmail
		);

		mockServer.givenThat(
				post(
						urlEqualTo(expectedUrl)
				).willReturn(
						aResponse().withStatus(204)
				)
		);

		//When
		chatroomService.assignUserToChatroom(testChatroomId, testUserEmail);

		//Then
		mockServer.verify(
				postRequestedFor(urlEqualTo(expectedUrl))
		);
	}

	@Test
	public void testUnassignUser() throws Exception {
		//Given
		String testChatroomId = UUID.randomUUID().toString();
		String testUserEmail = "auser@example.com";
		final String expectedUrl = format(
				"/rooms/%s/users/%s",
				testChatroomId,
				testUserEmail
		);

		mockServer.givenThat(
				delete(
						urlEqualTo(expectedUrl)
				).willReturn(
						aResponse().withStatus(204)
				)
		);

		//When
		chatroomService.unassignUserFromChatroom(testChatroomId, testUserEmail);

		//Then
		mockServer.verify(
				deleteRequestedFor(urlEqualTo(expectedUrl))
		);
	}
}
