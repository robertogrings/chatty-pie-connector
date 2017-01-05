package com.chattypie.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import com.appdirect.sdk.appmarket.events.APIResult;
import com.appdirect.sdk.appmarket.events.AddonSubscriptionOrder;
import com.chattypie.service.chattypie.chatroom.ChatroomService;

public class AddonSubscriptionOrderHandlerTest {
	private ChatroomService chatroomService = mock(ChatroomService.class);

	private AddonSubscriptionOrderHandler handler = new AddonSubscriptionOrderHandler(chatroomService);

	@Test
	public void testProcess_setUnlimitedHistoryOnChatroom() throws Exception {
		AddonSubscriptionOrder addonOrderEvent = addonOrderEvent("the-existing-chatroom-id");

		APIResult result = handler.handle(addonOrderEvent);

		assertThat(result.isSuccess()).isTrue();
		assertThat(result.getMessage()).isEqualTo("Successfully purchased add-on");
		assertThat(result.getAccountIdentifier()).isEqualTo("unlimited-history-the-existing-chatroom-id");

		verify(chatroomService).enableUnlimitedHistory("the-existing-chatroom-id");
	}

	private AddonSubscriptionOrder addonOrderEvent(String existingChatroomId) {
		return new AddonSubscriptionOrder(
				"some-key",
				null,
				null,
				null,
				null,
				null,
				existingChatroomId,
				null
		);
	}
}
