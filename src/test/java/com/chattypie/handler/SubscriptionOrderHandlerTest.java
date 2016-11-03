package com.chattypie.handler;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import com.appdirect.sdk.appmarket.api.APIResult;
import com.appdirect.sdk.appmarket.api.CompanyInfo;
import com.appdirect.sdk.appmarket.api.OrderInfo;
import com.appdirect.sdk.appmarket.api.SubscriptionOrder;
import com.appdirect.sdk.appmarket.api.UserInfo;
import com.chattypie.model.Account;
import com.chattypie.model.Chatroom;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionOrderHandlerTest {

	private SubscriptionOrderHandler subscriptionOrderHandler;

	@Mock
	private RestTemplate mockRestTemplate;
	private static final String MOCK_CHATTY_PIE_URL = "http://mock.url.org";

	@Before
	public void setUp() throws Exception {
		subscriptionOrderHandler = new SubscriptionOrderHandler(mockRestTemplate, MOCK_CHATTY_PIE_URL);
	}

	@Test
	public void testProcess_whenAccountCreatedSuccessfullyOnChattyPie_thenReturnTheCreatedAccountIdentifier() throws Exception {
		//Given
		String expectedAccountIdentifier = "expectedAccountIdentifier";
		String expectedChatroomName = "TestName";
		SubscriptionOrder testEvent = subscriptionOrderWithConfig(config("chatroomName", expectedChatroomName));

		String accountCreationEndpoint = format("%s/accounts", MOCK_CHATTY_PIE_URL);
		String chatroomCreationEndpoint = format("%s/accounts/%s/rooms", MOCK_CHATTY_PIE_URL, expectedAccountIdentifier);
		String expectedCreateAccountPayload = "{\"max_allowed_rooms\": 100}";
		String expectedChatroomCreationPayload = format("{\"name\": \"%s\"}", expectedChatroomName);

		when(mockRestTemplate.postForObject(eq(accountCreationEndpoint), eq(expectedCreateAccountPayload), eq(Account.class)))
				.thenReturn(new Account(expectedAccountIdentifier, 15));

		String expectedChatroomId = "some-id";
		when(
			mockRestTemplate.postForObject(eq(chatroomCreationEndpoint), eq(expectedChatroomCreationPayload), eq(Chatroom.class))
		)
		.thenReturn(
			new Chatroom(expectedChatroomId, expectedChatroomName, expectedAccountIdentifier)
		);

		//When
		APIResult apiResult = subscriptionOrderHandler.handle(testEvent);

		//Then
		assertThat(apiResult.isSuccess())
				.as("The returned API result is successful")
				.isTrue();
		assertThat(apiResult.getAccountIdentifier())
				.isEqualTo(expectedChatroomId);
	}

	private SubscriptionOrder subscriptionOrderWithConfig(Map<String, String> configuration) {
		return new SubscriptionOrder(null, UserInfo.builder().build(), configuration, CompanyInfo.builder().build(), OrderInfo.builder().build());
	}

	private Map<String, String> config(String... keyValues) {
		Map<String, String> config = new HashMap<>();
		for (int i = 0; i < keyValues.length; i++) {
			config.put(keyValues[i], keyValues[++i]);
		}
		return config;
	}
}
