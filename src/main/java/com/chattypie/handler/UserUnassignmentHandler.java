package com.chattypie.handler;

import com.appdirect.sdk.appmarket.AppmarketEventHandler;
import com.appdirect.sdk.appmarket.events.APIResult;
import com.appdirect.sdk.appmarket.events.ErrorCode;
import com.appdirect.sdk.appmarket.events.UserUnassignment;

public class UserUnassignmentHandler implements AppmarketEventHandler<UserUnassignment> {
	@Override
	public APIResult handle(UserUnassignment event) {
		return APIResult.failure(ErrorCode.OPERATION_CANCELLED, "This is not yet implemented");
	}
}
