/*
 * Copyright 2017 AppDirect, Inc. and/or its affiliates
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
