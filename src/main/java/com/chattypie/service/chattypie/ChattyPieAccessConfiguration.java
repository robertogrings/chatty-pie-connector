package com.chattypie.service.chattypie;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.chattypie.service.chattypie.account.ChattyPieAccountService;
import com.chattypie.service.chattypie.chatroom.ChatroomDao;
import com.chattypie.service.chattypie.chatroom.ChatroomService;
import com.chattypie.util.Delayer;
import com.querydsl.sql.SQLQueryFactory;

@Configuration
public class ChattyPieAccessConfiguration {

	@Value("${chatty.pie.host}")
	public String chattyPieHost;

	@Bean
	public RestTemplate chattyPieRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	public ChatroomDao chatroomDao(SQLQueryFactory queryFactory) {
		return new ChatroomDao(queryFactory);
	}

	@Bean
	public ChattyPieAccountService chattyPieAccountService() {
		return new ChattyPieAccountService(
				chattyPieRestTemplate(),
				chattyPieHost,
				new Delayer()
		);
	}

	@Bean
	public ChatroomService chatroomService(ChatroomDao chatroomDao) {
		return new ChatroomService(
				chattyPieRestTemplate(),
				chatroomDao,
				chattyPieHost
		);
	}
}
