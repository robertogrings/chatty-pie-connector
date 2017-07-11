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

import static com.appdirect.sdk.appmarket.events.APIResult.success;

import com.appdirect.sdk.appmarket.AppmarketEventHandler;
import com.appdirect.sdk.appmarket.events.APIResult;
import com.appdirect.sdk.appmarket.events.SubscriptionChange;

public class SubscriptionChangeHandler implements AppmarketEventHandler<SubscriptionChange> {
	@Override
	public APIResult handle(SubscriptionChange event) {
		//	event.getOwner() returns the user that owns the subscription: if it differs from the previous value known
		// to the connector, then it is an update. This is the "ownership change" use case

		//	event.getAccount() returns an object that contains information about the account and its status on the
		// Marketplace; Status changes could be retrieved this way

		//	event.getOrder() contains information about the order, most notably the edition
		//	event.getOrder().getItems() would return the order items. This would give information about seat changes:
		//	For example, if the order contains an item with 'quantity' = 5 and pricing unit USER, that would indicate
		//	a change of seats in the application. This is the "seat assign/unassign" use case
		return success("Change handled");
	}
}
