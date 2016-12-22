package com.chattypie.handler;

import static com.appdirect.sdk.appmarket.events.AccountStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;

import org.junit.Test;

import com.appdirect.sdk.appmarket.events.APIResult;
import com.appdirect.sdk.appmarket.events.AccountInfo;
import com.appdirect.sdk.appmarket.events.SubscriptionUpcomingInvoice;

public class SubscriptionUpcomingInvoiceHandlerTest {
	private SubscriptionUpcomingInvoiceHandler testtedHandler = new SubscriptionUpcomingInvoiceHandler();

	@Test
	public void handleUpcomingInvoiceEvent_whenReceived_thenReturnSuccess() throws Exception {
		//GIven
		SubscriptionUpcomingInvoice testEvent = new SubscriptionUpcomingInvoice(
				"some-key",
				AccountInfo.builder().status(ACTIVE).build(),
				new HashMap<>(),
				null);

		//When
		APIResult result = testtedHandler.handle(testEvent);

		//Then
		assertThat(result.isSuccess())
			.as("The result success code")
			.isTrue();
	}

}
