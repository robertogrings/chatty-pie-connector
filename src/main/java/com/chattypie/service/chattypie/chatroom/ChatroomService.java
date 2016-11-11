package com.chattypie.service.chattypie.chatroom;

import static java.lang.String.format;

import lombok.RequiredArgsConstructor;

import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class ChatroomService {
	private final RestTemplate restTemplate;
	private final String chattyPieHost;

	public Chatroom createChatroomForAccount(String accountId, String chatroomName) {
		return restTemplate.postForObject(
			format("%s/accounts/%s/rooms", chattyPieHost, accountId),
			format("{\"name\": \"%s\"}", chatroomName),
			Chatroom.class
		);
	}

	public void removeChatroom(String idOfChatroomToRemove) {
		restTemplate.delete(format("%s/rooms/%s", chattyPieHost, idOfChatroomToRemove));
	}
}
