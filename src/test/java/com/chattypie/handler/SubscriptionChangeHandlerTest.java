/*
 * Copyright 2017 AppDirect, Inc. and/or its affiliates
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
		return new SubscriptionChange(null, null, null, null, null, null, null, null);
	}

}
