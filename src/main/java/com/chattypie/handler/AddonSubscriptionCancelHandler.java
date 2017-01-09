package com.chattypie.handler;

import static com.appdirect.sdk.appmarket.events.APIResult.success;
import static java.lang.String.format;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.appdirect.sdk.appmarket.AppmarketEventHandler;
import com.appdirect.sdk.appmarket.events.APIResult;
import com.appdirect.sdk.appmarket.events.AddonSubscriptionCancel;
import com.chattypie.service.chattypie.chatroom.ChatroomService;

@Slf4j
@RequiredArgsConstructor
public class AddonSubscriptionCancelHandler implements AppmarketEventHandler<AddonSubscriptionCancel> {

	private final ChatroomService chatroomService;

	@Override
	public APIResult handle(AddonSubscriptionCancel event) {
		log.info("Handling an addon subscription cancel event for accountId={}", event.getAccountIdentifier());
		chatroomService.disableUnlimitedHistory(event.getParentAccountIdentifier());
		return success(
			format("Addon account with accountId=%s cancelled successfully", event.getAccountIdentifier())
		);
	}
}
