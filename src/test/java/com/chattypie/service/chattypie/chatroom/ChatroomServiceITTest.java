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
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import com.chattypie.support.ContentOf;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(MockitoJUnitRunner.class)
public class ChatroomServiceITTest {

	private ChatroomService testedChatroomService;

	@Mock
	private ChatroomDao chatroomDaoMock;

	@Rule
	public WireMockRule mockServer = new WireMockRule(options().dynamicPort());

	private ObjectMapper jsonMapper = new ObjectMapper();

	@Before
	public void setUp() throws Exception {

		String testChattyPieHost = format("http://localhost:%d", mockServer.port());
		testedChatroomService = new ChatroomService(new RestTemplate(), chatroomDaoMock, testChattyPieHost);
	}

	@Test
	public void testReadChatrooomByID_whenInvoked_theChattyPieEndpointIsCalled() throws Exception {
		//Given
		String testRoomId = "aghkZXZ-Tm9uZXIVCxIIQ2hhdHJvb20YgICAgICAgAkM";
		final String roomResourceUrl = format("/rooms/%s", testRoomId);

		final String expectedRoomPayload = ContentOf.resourceAsString("chatty-pie-responses/room.json");
		final Chatroom expectedChatroom = jsonMapper.readValue(expectedRoomPayload, Chatroom.class);

		mockServer.givenThat(
				get(
						urlEqualTo(roomResourceUrl)
				).willReturn(
						aResponse()
								.withStatus(200)
								.withHeader("Content-Type", "application/json")
								.withBody(expectedRoomPayload)
				)
		);

		//When
		final String actualChatroomAccountId = testedChatroomService.readIdOfAccountAssociatedWith(testRoomId);

		//Then
		assertThat(actualChatroomAccountId).isEqualTo(expectedChatroom.getAccountId());
	}
}
