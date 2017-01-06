package com.chattypie.handler;

import com.appdirect.sdk.appmarket.AppmarketEventHandler;
import com.appdirect.sdk.appmarket.events.APIResult;
import com.appdirect.sdk.appmarket.events.AddonSubscriptionCancel;

public class AddonSubscriptionCancelHandler implements AppmarketEventHandler<AddonSubscriptionCancel> {
	@Override
	public APIResult handle(AddonSubscriptionCancel event) {
		return null;
	}
}
