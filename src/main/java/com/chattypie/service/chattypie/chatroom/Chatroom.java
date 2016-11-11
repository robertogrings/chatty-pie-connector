package com.chattypie.service.chattypie.chatroom;

import lombok.Value;

import com.fasterxml.jackson.annotation.JsonProperty;

@Value
public class Chatroom {
	private String id;
	private String name;
	@JsonProperty("account_id")
	private String accountId;
}
