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

import static java.lang.String.format;

import lombok.RequiredArgsConstructor;

import com.appdirect.sdk.appmarket.AppmarketEventHandler;
import com.appdirect.sdk.appmarket.events.APIResult;
import com.appdirect.sdk.appmarket.events.UserUnassignment;
import com.chattypie.service.chattypie.chatroom.ChatroomMembershipService;

@RequiredArgsConstructor
public class UserUnassignmentHandler implements AppmarketEventHandler<UserUnassignment> {

	private final ChatroomMembershipService chatroomService;

	@Override
	public APIResult handle(UserUnassignment event) {
		final String chatroomToAsssignTo = event.getAccountId();
		final String assignedUserEmail = event.getUnassignedUser().getEmail();

		chatroomService.unassignUserFromChatroom(chatroomToAsssignTo, assignedUserEmail);

		return APIResult.success(format(
				"Successfully unassigned user with email %s from chatroom %s",
				assignedUserEmail,
				chatroomToAsssignTo
		));
	}
}
