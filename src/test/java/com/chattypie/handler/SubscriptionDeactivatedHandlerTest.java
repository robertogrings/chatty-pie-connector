package com.chattypie.handler;

import static com.appdirect.sdk.appmarket.api.AccountStatus.ACTIVE;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestClientException;

import com.appdirect.sdk.appmarket.api.APIResult;
import com.appdirect.sdk.appmarket.api.AccountInfo;
import com.appdirect.sdk.appmarket.api.SubscriptionDeactivated;
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
			new AccountInfo(testAppmarketAccountId, ACTIVE)
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
