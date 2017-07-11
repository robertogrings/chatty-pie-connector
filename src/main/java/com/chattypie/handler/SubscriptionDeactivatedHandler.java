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

import static com.appdirect.sdk.appmarket.events.APIResult.success;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.appdirect.sdk.appmarket.AppmarketEventHandler;
import com.appdirect.sdk.appmarket.events.APIResult;
import com.appdirect.sdk.appmarket.events.SubscriptionDeactivated;
import com.chattypie.service.chattypie.chatroom.ChatroomService;

@Slf4j
@RequiredArgsConstructor
public class SubscriptionDeactivatedHandler implements AppmarketEventHandler<SubscriptionDeactivated> {

	private final ChatroomService chatroomService;

	@Override
	public APIResult handle(SubscriptionDeactivated event) {
		chatroomService.suspendChatroom(event.getAccountInfo().getAccountIdentifier());
		return success("Account deactivated successfully");
	}
}
