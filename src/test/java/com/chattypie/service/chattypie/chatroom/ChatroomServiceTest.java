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
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import com.chattypie.persistence.model.ChatroomCreationRecord;
import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class ChatroomServiceTest {
	private ChatroomService testedChatroomService;
	private String mockChattyPieHost = "http://www.chatty-pie.ca";

	@Mock
	private RestTemplate mockRestTemplate;

	@Mock
	private ChatroomDao mockChatroomDao;

	@Before
	public void setUp() throws Exception {
		testedChatroomService = new ChatroomService(mockRestTemplate, mockChatroomDao, mockChattyPieHost);
	}

	@Test
	public void testSuspendChatroom_whenCalled_theCorrespondingRestCallIsMadeToChattyPie() throws Exception {
		//Given
		String testChatromId = "testChatromId";

		//When
		testedChatroomService.suspendChatroom(testChatromId);

		//Then
		verify(mockRestTemplate).put(
				eq(format("%s/rooms/%s", mockChattyPieHost, testChatromId)),
				eq("{\"status\":\"suspended\"}")
		);
	}

	@Test
	public void testReactivateChatroom_whenCalled_theCorrespondingRestCallIsMadeToChattyPie() throws Exception {
		//Given
		String testChatromId = "testChatromId";

		//When
		testedChatroomService.reactivate(testChatromId);

		//Then
		verify(mockRestTemplate).put(
				eq(format("%s/rooms/%s", mockChattyPieHost, testChatromId)),
				eq("{\"status\":\"active\"}")
		);
	}

	@Test
	public void createRoomForAccount() throws Exception {
		//Given
		String testChatroomName = "testName";
		String testAccountId = "accountId";
		String expectedChatroomId = "expectedChatroomId";
		Chatroom expectedCompany = Chatroom.builder().id(expectedChatroomId).name(testChatroomName).accountId(testAccountId).build();
		when(
				mockRestTemplate.postForObject(
						format("%s/accounts/%s/rooms", mockChattyPieHost, testAccountId),
						format("{\"name\": \"%s\"}", testChatroomName),
						Chatroom.class
				)
		).thenReturn(expectedCompany);

		//When
		Chatroom roomForAccount = testedChatroomService.createChatroomForAccount(testAccountId, testChatroomName);

		//Then
		assertThat(roomForAccount.getId()).isEqualTo(expectedChatroomId);
		assertThat(roomForAccount.getAccountId()).isEqualTo(testAccountId);
		verify(mockChatroomDao)
				.storeChatroom(eq(expectedChatroomId));
	}

	@Test
	public void testRemoveChatroom() {
		//Given
		String idOfChatroomToRemove = "dummyId";

		//When
		testedChatroomService.removeChatroom(idOfChatroomToRemove);

		//Then
		verify(mockRestTemplate)
				.delete(eq(format("%s/rooms/%s", mockChattyPieHost, idOfChatroomToRemove)));
	}

	@Test
	public void testEnableUnlimitedHistory() {
		testedChatroomService.enableUnlimitedHistory("some-chatroom-id");

		verify(mockRestTemplate).put(
				eq(format("%s/rooms/some-chatroom-id", mockChattyPieHost)),
				eq("{\"full_history_enabled\":\"true\"}")
		);
	}

	@Test
	public void testDisableUnlimitedHistory() throws Exception {
		//Given
		String testChatroomId = "testChatroomId";

		//When
		testedChatroomService.disableUnlimitedHistory(testChatroomId);

		//Then
		verify(mockRestTemplate).put(
				eq(format("%s/rooms/%s", mockChattyPieHost, testChatroomId)),
				eq("{\"full_history_enabled\":\"false\"}")
		);

	}

	@Test
	public void testChatroomsCreatedLastWeek_returnsTheInfoRetrievedFromTheUnderlyingDAO() throws Exception {
		//Given
		List<ChatroomCreationRecord> expectedChatrooms = Lists.newArrayList();
		ArgumentCaptor<ZonedDateTime> sinceDateCaptor = ArgumentCaptor.forClass(ZonedDateTime.class);
		when(mockChatroomDao.readCreatedSince(sinceDateCaptor.capture()))
				.thenReturn(expectedChatrooms);

		//When
		List<ChatroomCreationRecord> actualChatroomsCreated = testedChatroomService.chatroomsCreatedOverTheLastDay();

		//Then
		assertThat(actualChatroomsCreated).isEqualTo(expectedChatrooms);
	}

	@Test
	public void testReadIdOfAccountAssociatedWith_whenRestTemplateThrows_theExceptionIsWrappedAndReThrown() throws Exception {
		//Given
		String testChatroomId = UUID.randomUUID().toString();

		Throwable expectedException = new RuntimeException();

		when(
				mockRestTemplate.getForObject(
						String.format("%s/rooms/%s", mockChattyPieHost, testChatroomId),
						Chatroom.class
				)
		).thenThrow(
				expectedException
		);

		final String expectedErrorMessage = format("No chat room with id [%s] could be retrieved from %s. ",
				testChatroomId,
				mockChattyPieHost
		);

		//When
		final Throwable actualThrowable = catchThrowable(() ->
				testedChatroomService.readIdOfAccountAssociatedWith(testChatroomId)
		);

		//Then
		assertThat(actualThrowable.getCause()).isEqualTo(expectedException);
		assertThat(actualThrowable.getMessage()).isEqualTo(expectedErrorMessage);
	}
}
