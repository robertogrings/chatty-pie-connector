package com.chattypie.support;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.OutputStream;

import lombok.extern.slf4j.Slf4j;

import com.sun.net.httpserver.HttpExchange;

@Slf4j
class FakeServerResponses {
	static void methodNotAllowed(HttpExchange httpExchange) {
		sendJsonResponse(httpExchange, 405, "Method is not supported!");
	}

	static void sendJsonResponse(HttpExchange t, int statusCode, String response) {
		sendJsonResponse(t, statusCode, response.getBytes(UTF_8));
	}

	static void sendJsonResponse(HttpExchange t, int statusCode, byte[] response) {
		t.getResponseHeaders().add("Content-Type", "application/json");
		sendResponse(t, statusCode, response);
	}

	static void sendResponse(HttpExchange t, int statusCode, byte[] response) {
		log.debug("responding {} - {}", statusCode, new String(response));
		try {
			t.sendResponseHeaders(statusCode, response.length);
		} catch (IOException e) {
			log.error("exception occurred while sending response headers", e);
		}

		try (OutputStream os = t.getResponseBody()) {
			os.write(response);
		} catch (Exception e) {
			log.error("exception occurred while sending response body", e);
		}
	}
}
