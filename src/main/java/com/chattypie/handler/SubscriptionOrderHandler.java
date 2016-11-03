package com.chattypie.handler;

import static java.lang.String.format;

import java.util.UUID;

import org.springframework.web.client.RestTemplate;

import com.appdirect.sdk.appmarket.AppmarketEventHandler;
import com.appdirect.sdk.appmarket.api.APIResult;
import com.appdirect.sdk.appmarket.api.SubscriptionOrder;
import com.chattypie.model.Account;
import com.chattypie.model.Chatroom;

public class SubscriptionOrderHandler implements AppmarketEventHandler<SubscriptionOrder> {

	private final RestTemplate restTemplate;
	private final String chattyPieHost;

	public SubscriptionOrderHandler(RestTemplate restTemplate, String chattyPieHost) {
		this.restTemplate = restTemplate;
		this.chattyPieHost = chattyPieHost;
	}

	@Override
	public APIResult handle(SubscriptionOrder event) {
		String chatroomName = event.getConfiguration().getOrDefault("chatroomName", UUID.randomUUID().toString());
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
			chatroom.getId(),
			null,
			null
		);
	}
}
