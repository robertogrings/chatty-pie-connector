package com.chattypie.handler;

import lombok.RequiredArgsConstructor;

import com.appdirect.sdk.appmarket.AppmarketEventHandler;
import com.appdirect.sdk.appmarket.events.APIResult;
import com.appdirect.sdk.appmarket.events.AddonSubscriptionOrder;
import com.chattypie.service.chattypie.chatroom.ChatroomService;

@RequiredArgsConstructor
public class AddonSubscriptionOrderHandler implements AppmarketEventHandler<AddonSubscriptionOrder> {
	private final ChatroomService chatroomService;

	@Override
	public APIResult handle(AddonSubscriptionOrder event) {
		String existingChatroomId = extractChatroomIdFrom(event);

		chatroomService.enableUnlimitedHistory(existingChatroomId);

		APIResult result = APIResult.success("Successfully purchased add-on");
		result.setAccountIdentifier(generateAddonAccountIdFrom(existingChatroomId));
		return result;
	}

	private String generateAddonAccountIdFrom(String existingChatroomId) {
		return "unlimited-history-" + existingChatroomId;
	}

	private String extractChatroomIdFrom(AddonSubscriptionOrder addonOrderEvent) {
		return addonOrderEvent.getParentAccountIdentifier();
	}
}
