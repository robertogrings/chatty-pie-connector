package com.chattypie.service.chattypie;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.chattypie.service.chattypie.account.ChattyPieAccountService;
import com.chattypie.service.chattypie.chatroom.ChatroomService;

@Configuration
public class ChattyPieAccessConfiguration {

	@Value("${chatty.pie.host}")
	public String chattyPieHost;

	@Bean
	public RestTemplate chattyPieRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	public ChattyPieAccountService chattyPieAccountService() {
		return new ChattyPieAccountService(
			chattyPieRestTemplate(),
			chattyPieHost
		);
	}

	@Bean
	public ChatroomService chatroomService() {
		return new ChatroomService(
			chattyPieRestTemplate(),
			chattyPieHost
		);
	}
}
