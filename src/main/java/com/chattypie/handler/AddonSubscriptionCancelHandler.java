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
import static java.lang.String.format;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.appdirect.sdk.appmarket.AppmarketEventHandler;
import com.appdirect.sdk.appmarket.events.APIResult;
import com.appdirect.sdk.appmarket.events.AddonSubscriptionCancel;
import com.chattypie.service.chattypie.chatroom.ChatroomService;

@Slf4j
@RequiredArgsConstructor
public class AddonSubscriptionCancelHandler implements AppmarketEventHandler<AddonSubscriptionCancel> {

	private final ChatroomService chatroomService;

	@Override
	public APIResult handle(AddonSubscriptionCancel event) {
		log.info("Handling an addon subscription cancel event for accountId={}", event.getAccountIdentifier());
		chatroomService.disableUnlimitedHistory(event.getParentAccountIdentifier());
		return success(
			format("Addon account with accountId=%s cancelled successfully", event.getAccountIdentifier())
		);
	}
}
