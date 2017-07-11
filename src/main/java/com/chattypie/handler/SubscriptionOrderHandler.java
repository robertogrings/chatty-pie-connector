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

import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import com.appdirect.sdk.appmarket.AppmarketEventHandler;
import com.appdirect.sdk.appmarket.events.APIResult;
import com.appdirect.sdk.appmarket.events.SubscriptionOrder;
import com.chattypie.persistence.model.CompanyAccount;
import com.chattypie.service.appmarket.CompanyAccountService;
import com.chattypie.service.chattypie.chatroom.Chatroom;
import com.chattypie.service.chattypie.chatroom.ChatroomService;
import com.chattypie.service.chattypie.greeting.NotificationService;

@RequiredArgsConstructor
public class SubscriptionOrderHandler implements AppmarketEventHandler<SubscriptionOrder> {

	private static final String CHATROOM_FIELD_NAME = "chatroomName";

	private final CompanyAccountService companyAccountService;
	private final ChatroomService chatroomService;
	private final NotificationService notificationService;

	@Override
	public APIResult handle(SubscriptionOrder event) {
		String idOfCompanyPlacingTheOrder = event.getCompanyInfo().getUuid();
		Optional<CompanyAccount> existingCompanyAccount = companyAccountService.findExistingCompanyAccountById(idOfCompanyPlacingTheOrder);
		CompanyAccount companyAccount = existingCompanyAccount.orElseGet(
			() -> companyAccountService.createCompanyAccountFor(idOfCompanyPlacingTheOrder)
		);

		String chatroomName = event.getConfiguration().getOrDefault(CHATROOM_FIELD_NAME, UUID.randomUUID().toString());
		Chatroom chatroom = chatroomService.createChatroomForAccount(companyAccount.getAccountId(), chatroomName);

		if (!existingCompanyAccount.isPresent()) {
			notificationService.sendNewCompanyGreeting(event.getCompanyInfo());
		}

		APIResult success = APIResult.success(format("Successfully placed order for account: %s", companyAccount));
		success.setAccountIdentifier(chatroom.getId());
		return success;
	}
}
