package com.chattypie.service.chattypie.chatroom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the payload received by Chatty Pie on account creation
 */
@Value
@Builder
@AllArgsConstructor
public class Chatroom {
	private String id;
	private String name;
	private String type;
	private String status;
	@JsonProperty("full_history_enabled")
	private boolean fullHistoryEnabled;
	@JsonProperty("account_id")
	private String accountId;
}
