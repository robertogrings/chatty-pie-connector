package com.chattypie;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.appdirect.sdk.appmarket.api.APIResult;

public class ErrorHandlerTest {

	private ErrorHandler errorHandler = new ErrorHandler();

	@Test
	public void handleUncaughtErrors() throws Exception {
		//Given
		String testExceptionMessage = "testExceptionMessages";

		//When
		APIResult apiResult = errorHandler.handleUncaughtErrors(new Exception(testExceptionMessage));

		//Then
		assertThat(apiResult.isSuccess())
			.as("The API result success code")
			.isFalse();

		assertThat(apiResult.getMessage())
			.isEqualTo(testExceptionMessage);
	}

}
