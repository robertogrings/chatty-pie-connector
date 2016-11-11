package com.chattypie.service.chattypie.account;

import lombok.Value;

import com.fasterxml.jackson.annotation.JsonProperty;

@Value
public class ChattyPieAccount {
	private String id;
	@JsonProperty("max_allowed_rooms")
	private int maxAllowedRooms;
}
