package com.chattypie.web;


import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Condition;
import org.junit.Test;

import com.appdirect.sdk.appmarket.Credentials;
import com.chattypie.util.MapBuilder;

public class StringBackedCredentialsSupplierTest {
	private MapBuilder mapBuilder = mock(MapBuilder.class);

	@Test
	public void sendsTheAllowedCredentialsStringToTheMapBuilder() throws Exception {
		newCredentialsSupplier("some-creds");

		verify(mapBuilder).fromCommaDelimitedKeyValuePairs("some-creds");
	}

	@Test
	public void returnsGoodCredentials_whenKeyIsFound() throws Exception {
		when(mapBuilder.fromCommaDelimitedKeyValuePairs("key1:s1")).thenReturn(singletonMap("key1", "s1"));
		StringBackedCredentialsSupplier credentialsSupplier = newCredentialsSupplier("key1:s1");

		Credentials creds = credentialsSupplier.getConsumerCredentials("key1");
		assertThat(creds).is(credentialsOf("key1", "s1"));
	}

	@Test
	public void returnsInvalidCredentials_whenKeyIsNotFound() throws Exception {
		StringBackedCredentialsSupplier credentialsSupplier = newCredentialsSupplier("key1:s2");

		Credentials creds = credentialsSupplier.getConsumerCredentials("bad-key");
		assertThat(creds).is(credentialsOf("this key does not exist in the supplier", "this key does not exist in the supplier"));
	}

	private StringBackedCredentialsSupplier newCredentialsSupplier(String rawAllowedCredentials) {
		return new StringBackedCredentialsSupplier(rawAllowedCredentials, mapBuilder);
	}

	private Condition<Credentials> credentialsOf(String key, String secret) {
		return new Condition<>(c -> c.developerKey.equals(key) && c.developerSecret.equals(secret), "credentials of %s:%s", key, secret);
	}
}
