package com.chattypie.support;

import static com.chattypie.support.HttpClientHelper.aKubernetesHttpClient;
import static com.chattypie.support.HttpClientHelper.get;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

public class KubernetesCronAgent {
	private final String isvKey;
	private final String isvSecret;

	public KubernetesCronAgent(String isvKey, String isvSecret) {
		this.isvKey = isvKey;
		this.isvSecret = isvSecret;
	}

	public HttpResponse getSecureEndpoint(String endpoint) throws IOException, OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException, URISyntaxException {
		CloseableHttpClient httpClient = aKubernetesHttpClient();
		HttpGet request = get(endpoint);

		oauthSign(request);

		return httpClient.execute(request);
	}

	public HttpResponse getAnonymousEndpoint(String endpoint) throws IOException, OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException, URISyntaxException {
		CloseableHttpClient httpClient = aKubernetesHttpClient();
		HttpGet request = get(endpoint);

		return httpClient.execute(request);
	}

	private void oauthSign(HttpGet request) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(isvKey, isvSecret);
		consumer.sign(request);
	}
}
