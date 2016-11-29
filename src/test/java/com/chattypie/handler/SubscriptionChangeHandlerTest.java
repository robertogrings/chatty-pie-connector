package com.chattypie.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.appdirect.sdk.appmarket.events.APIResult;
import com.appdirect.sdk.appmarket.events.SubscriptionChange;

public class SubscriptionChangeHandlerTest {
	private SubscriptionChangeHandler testedHandler = new SubscriptionChangeHandler();

	@Test
	public void testHandleSubscriptionChange_alwaysReturnSuccess() throws Exception {
		//Given
		SubscriptionChange testEvent = someSubscriptionChange();

		//When
		APIResult result = testedHandler.handle(testEvent);

		//Then
		assertThat(result.isSuccess())
			.as("The result success code")
			.isTrue();
	}

	private SubscriptionChange someSubscriptionChange() {
		return new SubscriptionChange(null, null, null, null);
	}

}
