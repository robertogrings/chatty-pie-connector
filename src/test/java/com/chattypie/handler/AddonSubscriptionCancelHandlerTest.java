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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.appdirect.sdk.appmarket.events.APIResult;
import com.appdirect.sdk.appmarket.events.AddonSubscriptionCancel;
import com.chattypie.service.chattypie.chatroom.ChatroomService;

@RunWith(MockitoJUnitRunner.class)
public class AddonSubscriptionCancelHandlerTest {

	private AddonSubscriptionCancelHandler testedHandler;
	@Mock
	private ChatroomService mockChatroomService;

	@Before
	public void setUp() throws Exception {
		testedHandler = new AddonSubscriptionCancelHandler(mockChatroomService);

	}

	@Test
	public void testHandle_whenSubscriptionCancelCalledOnAddon_theUnlimitedHistoryOnTheParentAccountShouldBeDisabled() throws Exception {
		//Given
		String expectedAddonAccountId = "expectedAddonAccountId";
		String expectedParentAccountId = "expectedParentAccountId";
		AddonSubscriptionCancel testAddonSubscriptionCancelEvent = getAddonSubscriptionCancel(expectedAddonAccountId, expectedParentAccountId);
		String expectedHandlerMessage = format("Addon account with accountId=%s cancelled successfully", expectedAddonAccountId);

		//When
		APIResult result = testedHandler.handle(testAddonSubscriptionCancelEvent);

		//Then
		verify(mockChatroomService)
			.disableUnlimitedHistory(expectedParentAccountId);
		assertThat(result.getMessage()).isEqualTo(expectedHandlerMessage);
	}

	private AddonSubscriptionCancel getAddonSubscriptionCancel(String addonAccountId, String addonParentAccountId) {
		return new AddonSubscriptionCancel(
			addonAccountId, 
			addonParentAccountId, 
			null, 
			null,
			null, 
			null, 
			null
		);
	}
}
