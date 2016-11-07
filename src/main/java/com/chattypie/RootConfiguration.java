package com.chattypie;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import com.appdirect.sdk.ConnectorSdkConfiguration;
import com.appdirect.sdk.appmarket.AppmarketEventHandler;
import com.appdirect.sdk.appmarket.DeveloperSpecificAppmarketCredentials;
import com.appdirect.sdk.appmarket.DeveloperSpecificAppmarketCredentialsSupplier;
import com.appdirect.sdk.appmarket.api.SubscriptionCancel;
import com.appdirect.sdk.appmarket.api.SubscriptionOrder;
import com.chattypie.handler.SubscriptionCancelHandler;
import com.chattypie.handler.SubscriptionOrderHandler;

@Configuration
@Import(ConnectorSdkConfiguration.class)
public class RootConfiguration {

	@Value("${chatty.pie.host}")
	public String chattyPieHost;

	@Bean
	public DeveloperSpecificAppmarketCredentialsSupplier credentialsSupplier() {
		return () -> new DeveloperSpecificAppmarketCredentials("CPC-288", "xBzbtLgp1V7m");
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public AppmarketEventHandler<SubscriptionOrder> subscriptionOrderHandler() {
		return new SubscriptionOrderHandler(restTemplate(), chattyPieHost);
	}

	@Bean
	public AppmarketEventHandler<SubscriptionCancel> subscriptionCancelHandler() {
		return new SubscriptionCancelHandler(restTemplate(), chattyPieHost);
	}
}
