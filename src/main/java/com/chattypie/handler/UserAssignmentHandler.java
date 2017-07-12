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
import com.appdirect.sdk.appmarket.events.UserAssignment;
import com.chattypie.service.chattypie.chatroom.ChatroomMembershipService;
import com.chattypie.service.chattypie.chatroom.ChatroomService;

@RequiredArgsConstructor
public class UserAssignmentHandler implements AppmarketEventHandler<UserAssignment> {

	private final ChatroomMembershipService chatroomService;

	@Override
	public APIResult handle(UserAssignment event) {
		final String chatroomToAsssignTo = event.getAccountId();
		final String assignedUserEmail = event.getAssignedUser().getEmail();

		chatroomService.assignUserToChatroom(chatroomToAsssignTo, assignedUserEmail);
 
		return APIResult.success(format(
				"Successfully assigned user with email %s to chatroom %s",
				assignedUserEmail,
				chatroomToAsssignTo
		));
	}
}
