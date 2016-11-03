package com.chattypie;

import static com.appdirect.sdk.appmarket.api.ErrorCode.CONFIGURATION_ERROR;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import com.appdirect.sdk.ConnectorSdkConfiguration;
import com.appdirect.sdk.appmarket.AppmarketEventHandler;
import com.appdirect.sdk.appmarket.DeveloperSpecificAppmarketCredentials;
import com.appdirect.sdk.appmarket.DeveloperSpecificAppmarketCredentialsSupplier;
import com.appdirect.sdk.appmarket.api.APIResult;
import com.appdirect.sdk.appmarket.api.SubscriptionCancel;
import com.appdirect.sdk.appmarket.api.SubscriptionOrder;
import com.chattypie.handler.SubscriptionOrderHandler;

@Configuration
@Import(ConnectorSdkConfiguration.class)
public class RootConfiguration {

	@Value("${chatty.pie.host}")
	public String createAccountEndpoint;

	@Bean
	public DeveloperSpecificAppmarketCredentialsSupplier credentialsSupplier() {
		return () -> new DeveloperSpecificAppmarketCredentials("sample", "sample");
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public AppmarketEventHandler<SubscriptionOrder> subscriptionOrderHandler() {
		return new SubscriptionOrderHandler(restTemplate(), createAccountEndpoint);
	}

	@Bean
	public AppmarketEventHandler<SubscriptionCancel> subscriptionCancelHandler() {
		return event -> APIResult.failure(CONFIGURATION_ERROR, "SUB_CANCEL is not supported yet.");
	}
}
