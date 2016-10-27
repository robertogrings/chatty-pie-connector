package com.chattypie;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import com.appdirect.sdk.ConnectorSdkConfiguration;
import com.appdirect.sdk.appmarket.AppmarketEventProcessor;
import com.appdirect.sdk.appmarket.DeveloperSpecificAppmarketCredentials;
import com.appdirect.sdk.appmarket.DeveloperSpecificAppmarketCredentialsSupplier;
import com.chattypie.processor.SubscriptionOrderProcessor;

@Configuration
@Import(ConnectorSdkConfiguration.class)
public class RootConfiguration {

	@Value("${account.creation.endpoint}")
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
	public AppmarketEventProcessor subscriptionOrderProcessor() {
		return new SubscriptionOrderProcessor(restTemplate(), createAccountEndpoint);
	}
}

