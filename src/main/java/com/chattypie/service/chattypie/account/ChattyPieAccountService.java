package com.chattypie.service.chattypie.account;

import static java.lang.String.format;

import lombok.RequiredArgsConstructor;

import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class ChattyPieAccountService {
	private final RestTemplate restTemplate;
	private final String chattyPieHost;
	private static final int MAX_ALLOWED_ROOMS = 100;

	public ChattyPieAccount createChattyPieAccount() {
		return restTemplate.postForObject(
			format("%s/accounts", chattyPieHost),
			format("{\"max_allowed_rooms\": %d}", MAX_ALLOWED_ROOMS),
			ChattyPieAccount.class
		);
	}
}
