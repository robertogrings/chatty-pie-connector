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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.appdirect.sdk.appmarket.events.OrderValidationStatus;
import com.appdirect.sdk.appmarket.validation.AppmarketOrderValidationHandler;
import com.appdirect.sdk.appmarket.validation.ValidationResponse;
import com.chattypie.LocalizedMessageService;

public class OrderValidator implements AppmarketOrderValidationHandler {

	private final LocalizedMessageService localizedMessageService;

	public OrderValidator(LocalizedMessageService localizedMessageService) {
		this.localizedMessageService = localizedMessageService;
	}

	@Override
	public ValidationResponse validateOrderFields(Map<String, String> productSettings) {
		//Here are all the fields from the Chatty Pie product settings
		String alternateEmail = productSettings.getOrDefault("alternateEmail", "");
		String phoneNumber = productSettings.getOrDefault("phoneNumber", "");
		String customerDomain = productSettings.getOrDefault("customerDomain", "");
		String chatroomName = productSettings.getOrDefault("chatroomName", "");
		String organizationName = productSettings.getOrDefault("postalAddress.organizationName", "");
		String contactName = productSettings.getOrDefault("postalAddress.contactName", "");
		String locality = productSettings.getOrDefault("postalAddress.locality", "");
		String countryCode = productSettings.getOrDefault("postalAddress.countryCode", "");
		String addresLine1 = productSettings.getOrDefault("postalAddress.addressLine1", "");
		String region = productSettings.getOrDefault("postalAddress.region", "");

		Set<OrderValidationStatus> result = new HashSet<>();

		if (contactName.isEmpty()) {
			OrderValidationStatus build = OrderValidationStatus.builder()
					.title(localizedMessageService.get("contact.name.missing.title"))
					.description(localizedMessageService.get("contact.name.missing.message"))
					.field("postalAddress.contactName")
					.level("ERROR")
					.build();
			result.add(build);
		}

		if (chatroomName.isEmpty()) {
			OrderValidationStatus build = OrderValidationStatus.builder()
					.title(localizedMessageService.get("chatroom.name.missing.title"))
					.description(localizedMessageService.get("chatroom.name.missing.message"))
					.field("chatroomName")
					.level("WARN")
					.build();
			result.add(build);
		}
		return ValidationResponse.builder()
				.result(result)
				.build();
	}
}
