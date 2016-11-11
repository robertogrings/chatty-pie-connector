package com.chattypie.handler;

import static java.lang.String.format;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.client.RestClientException;

import com.appdirect.sdk.appmarket.AppmarketEventHandler;
import com.appdirect.sdk.appmarket.api.APIResult;
import com.appdirect.sdk.appmarket.api.SubscriptionCancel;
import com.chattypie.service.chattypie.chatroom.ChatroomService;

@Slf4j
@RequiredArgsConstructor
public class SubscriptionCancelHandler implements AppmarketEventHandler<SubscriptionCancel> {

	private final ChatroomService chatroomService;

	@Override
	public APIResult handle(SubscriptionCancel event) {
		String idOfChatroomToRemove = event.getAccountIdentifier();
		try {
			chatroomService.removeChatroom(idOfChatroomToRemove);
			return new APIResult(true, format("Account  %s cancelled successfully", idOfChatroomToRemove));
		} catch (RestClientException e) {
			String errorMessage = format("Account with accountId=%s could not be deleted", idOfChatroomToRemove);
			log.error(errorMessage, e);
			return new APIResult(false, errorMessage);
		}
	}
}
