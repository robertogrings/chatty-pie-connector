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

package com.chattypie.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.appdirect.sdk.appmarket.events.APIResult;
import com.appdirect.sdk.appmarket.events.UserInfo;
import com.appdirect.sdk.appmarket.events.UserUnassignment;
import com.chattypie.service.chattypie.chatroom.ChatroomMembershipService;

@RunWith(MockitoJUnitRunner.class)
public class UserUnassignmentHandlerTest {

	@Mock
	private ChatroomMembershipService mockChatroomMembershipService;
	private UserUnassignmentHandler testedEventHandler;

	@Before
	public void setUp() throws Exception {
		testedEventHandler = new UserUnassignmentHandler(mockChatroomMembershipService);
	}

	@Test
	public void handleUserAssign_shouldAssignUser() throws Exception {
		//Given
		String testAppmarketAccountId = "test-id-value";
		String testUserEmail = "theuser@example.com";
		UserUnassignment testCancelEvent = userAssignmentEvent(testAppmarketAccountId, testUserEmail);

		//When
		APIResult eventResponse = testedEventHandler.handle(testCancelEvent);

		//Then
		verify(mockChatroomMembershipService)
				.unassignUserFromChatroom(testAppmarketAccountId, testUserEmail);

		assertThat(eventResponse.isSuccess())
				.as("Event processed successfully")
				.isTrue();
	}

	private UserUnassignment userAssignmentEvent(String chatroomId, String testUserEmail) {
		UserInfo assignedUser = new UserInfo(
				"",
				"",
				testUserEmail,
				"",
				"",
				"",
				"",
				new HashMap<>()
		);
		return new UserUnassignment(
				assignedUser,
				chatroomId,
				"",
				new HashMap<>(),
				null,
				"",
				""
		);
	}
}
