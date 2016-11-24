package com.chattypie.service.chattypie.chatroom;

import static java.lang.String.format;

import lombok.RequiredArgsConstructor;

import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class ChatroomService {
	private static final String CHATROOM_RESOURCE_ENDPOINT_TEMPLATE = "%s/rooms/%s";

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
		restTemplate.delete(
			format(CHATROOM_RESOURCE_ENDPOINT_TEMPLATE, chattyPieHost, idOfChatroomToRemove)
		);
	}

	public void suspendChatroom(String idOfChatroomToSuspend) {
		restTemplate.put(
			format(CHATROOM_RESOURCE_ENDPOINT_TEMPLATE, chattyPieHost, idOfChatroomToSuspend),
			"{\"status\":\"suspended\"}"
		);
	}

	public void reactivate(String idOfChatroomToReactivate) {
		restTemplate.put(
			format(CHATROOM_RESOURCE_ENDPOINT_TEMPLATE, chattyPieHost, idOfChatroomToReactivate),
			"{\"status\":\"active\"}"
		);
	}
}
