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
import static java.time.ZonedDateTime.now;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.web.client.RestTemplate;

import com.chattypie.persistence.model.ChatroomCreationRecord;

@RequiredArgsConstructor
public class ChatroomService {
	private static final String CHATROOM_RESOURCE_ENDPOINT_TEMPLATE = "%s/rooms/%s";

	private final RestTemplate restTemplate;
	private final ChatroomDao chatroomDao;
	private final String chattyPieHost;

	public Chatroom createChatroomForAccount(String accountId, String chatroomName) {
		Chatroom createdChatroom = restTemplate.postForObject(
			format("%s/accounts/%s/rooms", chattyPieHost, accountId),
			format("{\"name\": \"%s\"}", chatroomName),
			Chatroom.class
		);
		chatroomDao.storeChatroom(createdChatroom.getId());
		return createdChatroom;
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

	public void enableUnlimitedHistory(String idOfChatroom) {
		restTemplate.put(
			format(CHATROOM_RESOURCE_ENDPOINT_TEMPLATE, chattyPieHost, idOfChatroom),
			"{\"full_history_enabled\":\"true\"}"
		);
	}

	public void disableUnlimitedHistory(String idOfChatroom) {
		restTemplate.put(
			format(CHATROOM_RESOURCE_ENDPOINT_TEMPLATE, chattyPieHost, idOfChatroom),
			"{\"full_history_enabled\":\"false\"}"
		);
	}

	public List<ChatroomCreationRecord> chatroomsCreatedOverTheLastDay() {
		return chatroomDao.readCreatedSince(now().minusDays(1));
	}
}
