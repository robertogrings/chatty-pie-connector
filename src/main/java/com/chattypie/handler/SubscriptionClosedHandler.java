package com.chattypie.handler;

import static com.appdirect.sdk.appmarket.api.APIResult.success;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.appdirect.sdk.appmarket.AppmarketEventHandler;
import com.appdirect.sdk.appmarket.api.APIResult;
import com.appdirect.sdk.appmarket.api.SubscriptionClosed;
import com.chattypie.service.chattypie.chatroom.ChatroomService;

@Slf4j
@RequiredArgsConstructor
public class SubscriptionClosedHandler implements AppmarketEventHandler<SubscriptionClosed> {

	private final ChatroomService chatroomService;

	@Override
	public APIResult handle(SubscriptionClosed event) {
		chatroomService.removeChatroom(event.getAccountInfo().getAccountIdentifier());
		return success("Account removed successfully");
	}
}
