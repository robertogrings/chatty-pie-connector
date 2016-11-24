package com.chattypie.handler;

import static com.appdirect.sdk.appmarket.api.APIResult.success;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.appdirect.sdk.appmarket.AppmarketEventHandler;
import com.appdirect.sdk.appmarket.api.APIResult;
import com.appdirect.sdk.appmarket.api.SubscriptionDeactivated;
import com.chattypie.service.chattypie.chatroom.ChatroomService;

@Slf4j
@RequiredArgsConstructor
public class SubscriptionDeactivatedHandler implements AppmarketEventHandler<SubscriptionDeactivated> {

	private final ChatroomService chatroomService;

	@Override
	public APIResult handle(SubscriptionDeactivated event) {
		chatroomService.suspendChatroom(event.getAccountInfo().getAccountIdentifier());
		return success("Account deactivated successfully");
	}
}
