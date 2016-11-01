package com.chattypie.processor;

import static com.appdirect.sdk.appmarket.api.EventType.SUBSCRIPTION_ORDER;
import static java.lang.String.format;

import java.util.UUID;

import org.springframework.web.client.RestTemplate;

import com.appdirect.sdk.appmarket.AppmarketEventProcessor;
import com.appdirect.sdk.appmarket.api.APIResult;
import com.appdirect.sdk.appmarket.api.EventInfo;
import com.appdirect.sdk.appmarket.api.EventType;
import com.chattypie.model.Account;
import com.chattypie.model.Chatroom;

public class SubscriptionOrderProcessor implements AppmarketEventProcessor {

	private final RestTemplate restTemplate;
	private final String chattyPieHost;

	public SubscriptionOrderProcessor(RestTemplate restTemplate, String chattyPieHost) {
		this.restTemplate = restTemplate;
		this.chattyPieHost = chattyPieHost;
	}

	@Override
	public boolean supports(EventType eventType) {
		return eventType == SUBSCRIPTION_ORDER;
	}

	@Override
	public APIResult process(EventInfo eventInfo, String s) {
		String chatroomName = eventInfo.getPayload().getConfiguration().getOrDefault("chatroomName", UUID.randomUUID().toString());
		Account accountCreated = restTemplate.postForObject(
			format("%s/accounts", chattyPieHost),
			"{\"max_allowed_rooms\": 100}",
			Account.class
		);

		Chatroom chatroom = restTemplate.postForObject(
			format("%s/accounts/%s/rooms", chattyPieHost, accountCreated.getId()),
			format("{\"name\": \"%s\"}", chatroomName),
			Chatroom.class
		);

		return new APIResult(
			true,
			false,
			null,
			format("Account created successfully: %s", accountCreated.toString()),
			chatroom.getAccountId(),
			null,
			null
		);
	}
}
