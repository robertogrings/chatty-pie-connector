package com.chattypie.web;

import static com.appdirect.sdk.appmarket.Credentials.invalidCredentials;

import java.util.Map;

import com.appdirect.sdk.appmarket.Credentials;
import com.appdirect.sdk.appmarket.DeveloperSpecificAppmarketCredentialsSupplier;
import com.chattypie.util.MapBuilder;

public class StringBackedCredentialsSupplier implements DeveloperSpecificAppmarketCredentialsSupplier {
	private final Map<String, String> allowedCredentials;

	public StringBackedCredentialsSupplier(String rawAllowedCredentials, MapBuilder mapBuilder) {
		this.allowedCredentials = mapBuilder.fromCommaDelimitedKeyValuePairs(rawAllowedCredentials);
	}

	@Override
	public Credentials getConsumerCredentials(String consumerKey) {
		String secret = allowedCredentials.get(consumerKey);
		return secret == null ? invalidCredentials() : new Credentials(consumerKey, secret);
	}
}
