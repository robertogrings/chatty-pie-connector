package com.chattypie.handler;

import static com.appdirect.sdk.appmarket.events.APIResult.success;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.appdirect.sdk.appmarket.AppmarketEventHandler;
import com.appdirect.sdk.appmarket.events.APIResult;
import com.appdirect.sdk.appmarket.events.SubscriptionCancel;
import com.chattypie.service.chattypie.chatroom.ChatroomService;

@Slf4j
@RequiredArgsConstructor
public class SubscriptionCancelHandler implements AppmarketEventHandler<SubscriptionCancel> {

	private final ChatroomService chatroomService;

	@Override
	public APIResult handle(SubscriptionCancel event) {
		chatroomService.suspendChatroom(event.getAccountIdentifier());
		return success("Account cancelled successfully");
	}
}
