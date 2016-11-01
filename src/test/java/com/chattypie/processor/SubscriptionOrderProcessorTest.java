package com.chattypie.processor;

import static com.appdirect.sdk.appmarket.api.EventType.SUBSCRIPTION_CANCEL;
import static com.appdirect.sdk.appmarket.api.EventType.SUBSCRIPTION_ORDER;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import com.appdirect.sdk.appmarket.api.APIResult;
import com.appdirect.sdk.appmarket.api.EventInfo;
import com.appdirect.sdk.appmarket.api.EventPayload;
import com.chattypie.model.Account;
import com.chattypie.model.Chatroom;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionOrderProcessorTest {

	private SubscriptionOrderProcessor subscriptionOrderProcessor;

	@Mock
	private RestTemplate mockRestTemplate;
	private static final String MOCK_CHATTY_PIE_URL = "http://mock.url.org";

	@Before
	public void setUp() throws Exception {
		subscriptionOrderProcessor = new SubscriptionOrderProcessor(mockRestTemplate, MOCK_CHATTY_PIE_URL);
	}

	@Test
	public void testSupports_whenCalledWithSubscriptionOrderEvent_thenReturnTrue() throws Exception {
		//When
		boolean result = subscriptionOrderProcessor.supports(SUBSCRIPTION_ORDER);

		//Then
		assertThat(result)
			.as("The processor supports SUBSCRIPTION_ORDER events")
			.isTrue();
	}

	@Test
	public void testSupports_whenCalledWithNonSubscriptionOrderEvent_thenReturnFalse() throws Exception {
		//When
		boolean result = subscriptionOrderProcessor.supports(SUBSCRIPTION_CANCEL);

		//Then
		assertThat(result)
			.as("The processor supports SUBSCRIPTION_CANCEL events")
			.isFalse();
	}

	@Test
	public void testProcess_whenAccountCreatedSuccessfullyOnChattyPie_thenReturnTheCreatedAccountIdentifier() throws Exception {
		//Given
		String expectedAccountIdentifier = "expectedAccountIdentifier";
		HashMap<String, String> configuration = new HashMap<>();
		String expectedChatroomName = "TestName";
		configuration.put("chatroomName", expectedChatroomName);
		EventInfo testEvent = EventInfo.builder()
			.payload(
				EventPayload.builder()
					.configuration(configuration)
				.build())
			.build();

		String accountCreationEndpoint = format("%s/accounts", MOCK_CHATTY_PIE_URL);
		String chatroomCreationEndpoint = format("%s/accounts/%s/rooms", MOCK_CHATTY_PIE_URL, expectedAccountIdentifier);
		String expectedCreateAccountPayload = "{\"max_allowed_rooms\": 100}";
		String expectedChatroomCreationPayload = format("{\"name\": \"%s\"}", expectedChatroomName);

		when(mockRestTemplate.postForObject(eq(accountCreationEndpoint), eq(expectedCreateAccountPayload), eq(Account.class)))
			.thenReturn(new Account(expectedAccountIdentifier, 15));

		when(mockRestTemplate.postForObject(eq(chatroomCreationEndpoint), eq(expectedChatroomCreationPayload), eq(Chatroom.class)))
			.thenReturn(
				new Chatroom(null, null, expectedAccountIdentifier)
			);

		//When
		APIResult apiResult = subscriptionOrderProcessor.process(testEvent, "");

		//Then
		assertThat(apiResult.isSuccess())
			.as("The returned API result is successful")
			.isTrue();
		assertThat(apiResult.getAccountIdentifier())
			.isEqualTo(expectedAccountIdentifier);
	}

}
