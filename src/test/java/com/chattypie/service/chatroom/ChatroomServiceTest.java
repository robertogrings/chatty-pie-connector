package com.chattypie.service.chatroom;

import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import com.chattypie.persistence.model.ChatroomCreationRecord;
import com.chattypie.service.chattypie.chatroom.Chatroom;
import com.chattypie.service.chattypie.chatroom.ChatroomDao;
import com.chattypie.service.chattypie.chatroom.ChatroomService;
import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class ChatroomServiceTest {
	private ChatroomService testedChatroomService;
	private String mockChattyPieHost = "http://www.google.ca";

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
		Chatroom expectedCompany = new Chatroom(expectedChatroomId, testChatroomName, testAccountId);
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
	public void testChatroomsCreatedLastWeek_returnsTheInfoRetrievedFromTheUnderlyingDAO() throws Exception {
		//Given
		List<ChatroomCreationRecord> expectedChatrooms = Lists.newArrayList();
		ArgumentCaptor<ZonedDateTime> sinceDateCaptor = ArgumentCaptor.forClass(ZonedDateTime.class);
		when(mockChatroomDao.readCreatedSince(sinceDateCaptor.capture()))
			.thenReturn(expectedChatrooms);

		ZonedDateTime testStartTime = now();

		//When
		List<ChatroomCreationRecord> actualChatroomsCreated = testedChatroomService.chatroomsCreatedOverTheLastDay();

		//Then
		assertThat(actualChatroomsCreated).isEqualTo(expectedChatrooms);
	}
}
