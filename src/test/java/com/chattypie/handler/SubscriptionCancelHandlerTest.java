package com.chattypie.handler;

import static java.lang.String.format;
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
import org.springframework.web.client.RestTemplate;

import com.appdirect.sdk.appmarket.api.APIResult;
import com.appdirect.sdk.appmarket.api.SubscriptionCancel;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionCancelHandlerTest {

	@Mock
	private RestTemplate mockRestTemplate;
	private SubscriptionCancelHandler testedEventHandler;
	private String mockChattyPieHost = "www.chattypie.example.org";

	@Before
	public void setUp() throws Exception {
		testedEventHandler = new SubscriptionCancelHandler(mockRestTemplate, mockChattyPieHost);
	}

	@Test
	public void handleSubscriptionCancel_shouldSendDeleteChatroomMessageToChattyPie() throws Exception {
		//Given
		String testAppmarketAccountId = "test-id-value";
		SubscriptionCancel testCancelEvent = new SubscriptionCancel(testAppmarketAccountId);

		//When
		APIResult eventResponse = testedEventHandler.handle(testCancelEvent);

		//Then
		verify(mockRestTemplate)
			.delete(eq(format("%s/rooms/%s", mockChattyPieHost, testAppmarketAccountId)));

		assertThat(eventResponse.isSuccess())
			.as("Event processed successfully")
			.isTrue();
	}

	@Test
	public void handleSubscriptionCancel_whenDeleteFails_shouldReturnAnInformativeMessage() throws Exception {
		//Given
		String testAppmarketAccountId = "test-id-value";
		SubscriptionCancel testCancelEvent = new SubscriptionCancel(testAppmarketAccountId);
		doThrow(RestClientException.class)
			.when(mockRestTemplate).delete(anyString());

		//When
		APIResult eventResponse = testedEventHandler.handle(testCancelEvent);

		//Then
		assertThat(eventResponse.isSuccess())
			.as("Event processed successfully")
			.isFalse();
		assertThat(eventResponse.getMessage())
			.isEqualTo("Account with accountId=test-id-value could not be deleted");
	}

}
