package com.chattypie.processor;

import static com.appdirect.sdk.appmarket.api.EventType.SUBSCRIPTION_ORDER;
import static java.lang.String.format;

import org.springframework.web.client.RestTemplate;

import com.appdirect.sdk.appmarket.AppmarketEventProcessor;
import com.appdirect.sdk.appmarket.api.APIResult;
import com.appdirect.sdk.appmarket.api.EventInfo;
import com.appdirect.sdk.appmarket.api.EventType;
import com.chattypie.model.Account;

public class SubscriptionOrderProcessor implements AppmarketEventProcessor {

	private final RestTemplate restTemplate;
	private final String createAccountEndpointUrl;

	public SubscriptionOrderProcessor(RestTemplate restTemplate, String createAccountEndpointUrl) {
		this.restTemplate = restTemplate;
		this.createAccountEndpointUrl = createAccountEndpointUrl;
	}

	@Override
	public boolean supports(EventType eventType) {
		return eventType == SUBSCRIPTION_ORDER;
	}

	@Override
	public APIResult process(EventInfo eventInfo, String s) {
		Account accountCreated = restTemplate.postForObject(
			createAccountEndpointUrl,
			"{\"max_allowed_rooms\": 100}",
			Account.class
		);

		return new APIResult(
			true,
			false,
			null,
			format("Account created successfully: %s", accountCreated.toString()),
			accountCreated.getId(),
			null,
			null
		);
	}
}
