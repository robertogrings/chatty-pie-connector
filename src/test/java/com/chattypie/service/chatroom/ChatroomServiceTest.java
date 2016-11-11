package com.chattypie.service.chatroom;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import com.chattypie.service.chattypie.chatroom.Chatroom;
import com.chattypie.service.chattypie.chatroom.ChatroomService;

@RunWith(MockitoJUnitRunner.class)
public class ChatroomServiceTest {
	private ChatroomService testedChatroomService;
	private String mockChattyPieHost = "http://www.google.ca";

	@Mock
	private RestTemplate mockRestTemplate;

	@Before
	public void setUp() throws Exception {
		testedChatroomService = new ChatroomService(mockRestTemplate, mockChattyPieHost);
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
	}

}
