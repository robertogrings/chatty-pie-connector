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

import static com.appdirect.sdk.appmarket.events.AccountStatus.CANCELLED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.appdirect.sdk.appmarket.events.APIResult;
import com.appdirect.sdk.appmarket.events.AccountInfo;
import com.appdirect.sdk.appmarket.events.SubscriptionClosed;
import com.chattypie.service.chattypie.chatroom.ChatroomService;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionClosedHandlerTest {

	@Mock
	private ChatroomService mockChatroomService;

	private SubscriptionClosedHandler testedHandler;

	@Before
	public void setUp() throws Exception {
		testedHandler = new SubscriptionClosedHandler(mockChatroomService);
	}

	@Test
	public void testHandle_whenSubscriptionClosedEventHandled_thenTheCorrespondingChatroomIsRemoved() throws Exception {
		//Given
		String testChatroomIdentifier = "testAccountIdentifier";
		SubscriptionClosed testSubscriptionEvent = new SubscriptionClosed(
				"some-key",
				AccountInfo.builder()
						.accountIdentifier(testChatroomIdentifier)
						.status(CANCELLED).build(),
				new HashMap<>(),
				null,
				null,
				null
		);

		//When
		APIResult result = testedHandler.handle(testSubscriptionEvent);

		//Then
		assertThat(result.isSuccess()).isTrue();
		verify(mockChatroomService).removeChatroom(testChatroomIdentifier);
	}

}
