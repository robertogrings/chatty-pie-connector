package com.chattypie.handler;

import com.appdirect.sdk.appmarket.AppmarketEventHandler;
import com.appdirect.sdk.appmarket.events.APIResult;
import com.appdirect.sdk.appmarket.events.ErrorCode;
import com.appdirect.sdk.appmarket.events.UserAssignment;

public class UserAssignmentHandler implements AppmarketEventHandler<UserAssignment> {
	@Override
	public APIResult handle(UserAssignment event) {
		return APIResult.failure(ErrorCode.OPERATION_CANCELLED, "This is not yet implemented");
	}
}
