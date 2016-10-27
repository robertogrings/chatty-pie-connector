package com.chattypie.model;

import lombok.Value;

import com.fasterxml.jackson.annotation.JsonProperty;

@Value
public class Account {
	private String id;
	@JsonProperty("max_allowed_rooms")
	private int maxAllowedRooms;
}
