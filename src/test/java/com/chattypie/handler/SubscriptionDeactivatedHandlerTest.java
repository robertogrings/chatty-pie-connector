package com.chattypie.handler;

import static com.appdirect.sdk.appmarket.events.AccountStatus.ACTIVE;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.appdirect.sdk.appmarket.events.APIResult;
import com.appdirect.sdk.appmarket.events.AccountInfo;
import com.appdirect.sdk.appmarket.events.SubscriptionDeactivated;
import com.chattypie.service.chattypie.chatroom.ChatroomService;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionDeactivatedHandlerTest {

	@Mock
	private ChatroomService mockChatroomService;
	private SubscriptionDeactivatedHandler testedEventHandler;

	@Before
	public void setUp() throws Exception {
		testedEventHandler = new SubscriptionDeactivatedHandler(mockChatroomService);
	}

	@Test
	public void handleSubscriptionDeactivated_shouldSuspendChatroom() throws Exception {
		//Given
		String testAppmarketAccountId = "test-id-value";
		SubscriptionDeactivated testCancelEvent = new SubscriptionDeactivated(
				"some-key",
				AccountInfo.builder()
						.accountIdentifier(testAppmarketAccountId)
						.status(ACTIVE).build(),
				new HashMap<>(),
				null
		);

		//When
		APIResult eventResponse = testedEventHandler.handle(testCancelEvent);

		//Then
		verify(mockChatroomService)
			.suspendChatroom(eq(testAppmarketAccountId));

		assertThat(eventResponse.isSuccess())
			.as("Event processed successfully")
			.isTrue();
	}
}
