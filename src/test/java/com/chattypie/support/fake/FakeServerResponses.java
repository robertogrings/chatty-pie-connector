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
			boolean statusIs204 = statusCode == 204;
			t.sendResponseHeaders(statusCode, statusIs204 ? -1 : response.length);
			if (statusIs204) {
				return; // 204 has no body, client will close connection as soon as headers are sent.
			}
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
