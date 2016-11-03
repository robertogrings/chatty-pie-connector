package com.chattypie.handler;

import static java.lang.String.format;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.appdirect.sdk.appmarket.AppmarketEventHandler;
import com.appdirect.sdk.appmarket.api.APIResult;
import com.appdirect.sdk.appmarket.api.SubscriptionCancel;

@Slf4j
public class SubscriptionCancelHandler implements AppmarketEventHandler<SubscriptionCancel> {

	private final RestTemplate restTemplate;
	private final String chattyPieHost;

	public SubscriptionCancelHandler(RestTemplate restTemplate, String chattyPieHost) {
		this.restTemplate = restTemplate;
		this.chattyPieHost = chattyPieHost;
	}

	@Override
	public APIResult handle(SubscriptionCancel event) {
		String idOfChatroomToRemove = event.getAccountIdentifier();
		try {
			restTemplate.delete(format("%s/rooms/%s", chattyPieHost, idOfChatroomToRemove));
			return new APIResult(true, format("Account  %s cancelled successfully", idOfChatroomToRemove));
		} catch (RestClientException e) {
			String errorMessage = format("Account with accountId=%s could not be deleted", idOfChatroomToRemove);
			log.error(errorMessage, e);
			return new APIResult(false, errorMessage);
		}
	}
}
