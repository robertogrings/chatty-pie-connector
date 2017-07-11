/*
 * Copyright 2017 AppDirect, Inc. and/or its affiliates
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chattypie.support.fake;

import static com.chattypie.support.ContentOf.resourceAsString;
import static com.chattypie.support.ContentOf.streamAsString;
import static com.chattypie.support.HttpClientHelper.anAppmarketHttpClient;
import static com.chattypie.support.HttpClientHelper.get;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

import lombok.RequiredArgsConstructor;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

public class FakeAppmarket {
	private final HttpServer server;
	private final String isvKey;
	private final String isvSecret;
	private final List<String> allRequestPaths;
	private final List<String> resolvedEvents;
	private final Object resolvedEventsLock = new Object();
	private String lastRequestBody;
	private Throwable backgroundThreadException;

	public static FakeAppmarket create(int port, String isvKey, String isvSecret) throws ExecutionException, InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<HttpServer> futureServer = executor.submit(() -> HttpServer.create(new InetSocketAddress(port), 10));
		return new FakeAppmarket(futureServer.get(), isvKey, isvSecret);
	}

	private FakeAppmarket(HttpServer server, String isvKey, String isvSecret) {
		this.server = server;
		this.isvKey = isvKey;
		this.isvSecret = isvSecret;
		this.allRequestPaths = new ArrayList<>();
		this.resolvedEvents = new ArrayList<>();
	}

	public FakeAppmarket start() {
		Predicate<HttpExchange> oauthInTheHeader = httpExchange -> {
			String authorization = httpExchange.getRequestHeaders().getFirst("Authorization");
			return authorization != null && authorization.startsWith("OAuth oauth_consumer_key=\"" + isvKey + "\",");
		};

		server.createContext("/v1/events/", new ReturnResourceContent(oauthInTheHeader));
		server.createContext("/api/integration/v1/events/", new OauthSecuredHandler(oauthInTheHeader) {
			@Override
			byte[] buildJsonResponse(URI requestUri) throws IOException {
				String eventId = requestUri.getPath().split("/")[5];
				markEventAsResolved(eventId);
				return "".getBytes(UTF_8);
			}
		});

		server.start();
		return this;
	}

	public void stop() throws Exception {
		server.stop(0);
		if (backgroundThreadException != null) {
			IllegalThreadStateException illegalThreadStateException = new IllegalThreadStateException("One of the FakeAppMarket's request thread threw an exception. This is bad.");
			illegalThreadStateException.initCause(backgroundThreadException);
			throw illegalThreadStateException;
		}
	}

	public List<String> allRequestPaths() {
		return new ArrayList<>(allRequestPaths);
	}

	public String lastRequestBody() {
		return lastRequestBody;
	}

	public List<String> resolvedEvents() {
		return new ArrayList<>(resolvedEvents);
	}

	private void markEventAsResolved(String eventId) {
		synchronized (resolvedEventsLock) {
			resolvedEvents.add(eventId);
			resolvedEventsLock.notify();
		}
	}

	public HttpResponse sendEventTo(String connectorEventEndpointUrl, String appmarketEventPath) throws Exception {
		CloseableHttpClient httpClient = anAppmarketHttpClient();
		HttpGet request = get(connectorEventEndpointUrl, "eventUrl", baseAppmarketUrl() + appmarketEventPath);

		oauthSign(request);

		return httpClient.execute(request);
	}

	private String baseAppmarketUrl() {
		return "http://localhost:" + server.getAddress().getPort();
	}

	private void oauthSign(HttpGet request) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(isvKey, isvSecret);
		consumer.sign(request);
	}

	public void waitForResolvedEvents(int desiredNumberOfResolvedEvents) throws Exception {
		long maxNumberOfTries = 200, timeoutOfOneTryMs = 50;
		synchronized (resolvedEventsLock) {
			int tries = 0;
			while (resolvedEvents.size() < desiredNumberOfResolvedEvents && tries < maxNumberOfTries) {
				tries++;
				resolvedEventsLock.wait(timeoutOfOneTryMs);
			}

			if (tries == maxNumberOfTries) {
				throw new TimeoutException("Could not find " + desiredNumberOfResolvedEvents + " resolved event after trying for " + maxNumberOfTries * timeoutOfOneTryMs + "ms. | resolvedEvents: " + resolvedEvents);
			}
		}
	}

	class ReturnResourceContent extends OauthSecuredHandler {
		ReturnResourceContent(Predicate<HttpExchange> authorized) {
			super(authorized);
		}

		@Override
		byte[] buildJsonResponse(URI requestUri) throws IOException {
			String jsonResource = buildJsonResourceFrom(requestUri);
			return resourceAsString(jsonResource)
					.replace("{{fake-appmarket-url}}", baseAppmarketUrl())
					.replace("{{account-id}}", getOptionalAccountIdParam(requestUri))
					.getBytes(UTF_8);
		}

		private String buildJsonResourceFrom(URI requestUri) {
			String[] fragments = requestUri.getPath().split("/");
			String resourceName = fragments[fragments.length - 1];
			return "events/subscription-" + resourceName + ".json";
		}

		private String getOptionalAccountIdParam(URI requestURI) {
			String query = requestURI.getQuery();
			if (query == null || !query.startsWith("account-id")) {
				return "";
			}
			return query.split("=")[1];
		}
	}

	@RequiredArgsConstructor
	abstract class OauthSecuredHandler implements HttpHandler {
		private final Predicate<HttpExchange> authorized;

		@Override
		public void handle(HttpExchange t) throws IOException {
			byte[] response = "".getBytes(UTF_8);
			try {
				allRequestPaths.add(t.getRequestURI().toString());

				if (!authorized.test(t)) {
					FakeServerResponses.sendResponse(t, 401, "UNAUTHORIZED! Use OAUTH!".getBytes(UTF_8));
					return;
				}
				lastRequestBody = streamAsString(t.getRequestBody());

				response = buildJsonResponse(t.getRequestURI());
			} catch (Exception e) {
				backgroundThreadException = e;
			} finally {
				FakeServerResponses.sendJsonResponse(t, 200, response);
			}
		}

		abstract byte[] buildJsonResponse(URI requestUri) throws IOException;
	}
}
