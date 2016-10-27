package com.chattypie.processor;

import static com.appdirect.sdk.appmarket.api.EventType.SUBSCRIPTION_CANCEL;
import static com.appdirect.sdk.appmarket.api.EventType.SUBSCRIPTION_ORDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import com.appdirect.sdk.appmarket.api.APIResult;
import com.appdirect.sdk.appmarket.api.EventInfo;
import com.chattypie.model.Account;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionOrderProcessorTest {

	private SubscriptionOrderProcessor subscriptionOrderProcessor;

	@Mock
	private RestTemplate mockRestTemplate;
	private static final String MOCK_ENDPOINT_URL = "http://mock.url.org";

	@Before
	public void setUp() throws Exception {
		subscriptionOrderProcessor = new SubscriptionOrderProcessor(mockRestTemplate, MOCK_ENDPOINT_URL);
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
		EventInfo testEvent = EventInfo.builder().build();
		when(mockRestTemplate.postForObject(eq(MOCK_ENDPOINT_URL), anyString(), eq(Account.class)))
			.thenReturn(new Account(expectedAccountIdentifier, 15));

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
