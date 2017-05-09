package com.chattypie;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.appdirect.sdk.appmarket.validation.AppmarketOrderValidationHandler;
import com.chattypie.handler.OrderValidator;

@Configuration
public class ValidationEndpointConfiguration {

	@Primary
	@Bean
	public AppmarketOrderValidationHandler validationHandler(LocalizedMessageService localizedMessageService) {
		return new OrderValidator(localizedMessageService);
	}
}
