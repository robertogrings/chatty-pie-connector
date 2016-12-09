package com.chattypie.service.chattypie.account;

import static java.lang.String.format;
import static java.time.Duration.ofSeconds;

import lombok.RequiredArgsConstructor;

import org.springframework.web.client.RestTemplate;

import com.chattypie.util.Delayer;

@RequiredArgsConstructor
public class ChattyPieAccountService {
	private static final int MAX_ALLOWED_ROOMS = 100;

	private final RestTemplate restTemplate;
	private final String chattyPieHost;
	private final Delayer delayer;

	public ChattyPieAccount createChattyPieAccount() {
		ChattyPieAccount chattyPieAccount = restTemplate.postForObject(
				format("%s/accounts", chattyPieHost),
				format("{\"max_allowed_rooms\": %d}", MAX_ALLOWED_ROOMS),
				ChattyPieAccount.class
		);

		waitUntilAccountIsPropagatedOnTheirNetwork();

		return chattyPieAccount;
	}

	private void waitUntilAccountIsPropagatedOnTheirNetwork() {
		delayer.delayFor(ofSeconds(5));
	}
}
