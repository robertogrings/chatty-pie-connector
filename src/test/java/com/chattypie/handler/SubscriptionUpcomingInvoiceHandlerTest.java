package com.chattypie.handler;

import static com.appdirect.sdk.appmarket.api.AccountStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.appdirect.sdk.appmarket.api.APIResult;
import com.appdirect.sdk.appmarket.api.AccountInfo;
import com.appdirect.sdk.appmarket.api.SubscriptionUpcomingInvoice;

public class SubscriptionUpcomingInvoiceHandlerTest {
	private SubscriptionUpcomingInvoiceHandler testtedHandler = new SubscriptionUpcomingInvoiceHandler();

	@Test
	public void handleUpcomingInvoiceEvent_whenReceived_thenReturnSuccess() throws Exception {
		//GIven
		SubscriptionUpcomingInvoice testEvent = new SubscriptionUpcomingInvoice(
			new AccountInfo("", ACTIVE)
		);

		//When
		APIResult result = testtedHandler.handle(testEvent);

		//Then
		assertThat(result.isSuccess())
			.as("The result success code")
			.isTrue();
	}

}
