package com.chattypie;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.appdirect.sdk.appmarket.api.APIResult;
import com.appdirect.sdk.appmarket.api.ErrorCode;

@ControllerAdvice
@RestController
public class ErrorHandler {

	@ExceptionHandler(value = Exception.class)
	public APIResult handleUncaughtErrors(Exception e) {
		return new APIResult(ErrorCode.UNKNOWN_ERROR, e.getMessage());
	}
}
