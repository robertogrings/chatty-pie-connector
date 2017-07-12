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

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class ChatroomMembershipServiceTest {
	private ChatroomMembershipService testedChatroomMembershipService;
	private String mockChattyPieHost = "http://www.google.ca";

	@Mock
	private RestTemplate mockRestTemplate;

	@Mock
	private ChatroomDao mockChatroomDao;

	@Before
	public void setUp() throws Exception {
		testedChatroomMembershipService = new ChatroomMembershipService(mockRestTemplate, mockChattyPieHost);
	}


	@Test
	public void testAssignUserToChatroom_whenUserIsAssigned_thenAPostIsSendToTheChattyPieApiResource() throws Exception {
		//Given
		String testChatroomId = "testChatroomId";
		String testAssignedUserEmail = "dummy@example.com";

		//When
		testedChatroomMembershipService.assignUserToChatroom(testChatroomId, testAssignedUserEmail);

		//Then
		verify(mockRestTemplate).postForLocation(
				eq(format("%s/rooms/%s/users/%s", mockChattyPieHost, testChatroomId, testAssignedUserEmail)),
				eq("")
		);
	}

	@Test
	public void testUnassignUserToChatroom_whenUserIsUnassigned_thenADeleteIsSendToTheChattyPieApiResource() throws Exception {
		//Given
		String testChatroomId = "testChatroomId";
		String testAssignedUserEmail = "dummy@example.com";

		//When
		testedChatroomMembershipService.unassignUserFromChatroom(testChatroomId, testAssignedUserEmail);

		//Then
		verify(mockRestTemplate).delete(
				eq(format("%s/rooms/%s/users/%s", mockChattyPieHost, testChatroomId, testAssignedUserEmail))
		);
	}
}
