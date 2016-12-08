package com.chattypie.support;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class FakeChattyPie {
	private final static Logger LOG = LoggerFactory.getLogger(FakeChattyPie.class);

	private final HttpServer server;
	private final List<String> allRequestPaths;
	private String lastRequestBody;
	private String supportedAccountId;

	public static FakeChattyPie create(int port) throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		return new FakeChattyPie(server);
	}

	private FakeChattyPie(HttpServer server) {
		this.server = server;
		this.allRequestPaths = new ArrayList<>();
	}

	public FakeChattyPie start() {
		final Consumer<HttpExchange> createAccount = httpExchange -> sendJsonResponse(httpExchange, 201, accountJson(supportedAccountId));
		final Consumer<HttpExchange> createRoom = httpExchange -> {
			String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
			String requestedAccountId = pathParts[2];
			if (!requestedAccountId.equals(supportedAccountId)) {
				sendJsonResponse(httpExchange, 400, format("account %s does not exist", requestedAccountId));
				return;
			}
			sendJsonResponse(httpExchange, 201, roomJson("room-of-" + requestedAccountId, requestedAccountId));
		};

		server.createContext("/accounts", httpExchange -> {
			if (supportedAccountId == null) {
				sendJsonResponse(httpExchange, 500, "{\"message\":\"FakeChattyPie - call setSupportedAccountId() before making calls to it.\"}");
				return;
			}

			allRequestPaths.add(httpExchange.getRequestURI().toString());

			String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
			String lastPathPart = pathParts[pathParts.length - 1];
			String requestMethod = httpExchange.getRequestMethod();
			if ("POST".equals(requestMethod)) {
				if (lastPathPart.equals("accounts")) {
					createAccount.accept(httpExchange);
				} else {
					createRoom.accept(httpExchange);
				}
				return;
			}
			sendJsonResponse(httpExchange, 405, "Method is not supported!");
		});

		server.start();
		return this;
	}

	public void stop() {
		server.stop(0);
	}

	public List<String> allRequestPaths() {
		return new ArrayList<>(allRequestPaths);
	}

	public void setSupportedAccountId(String supportedAccountId) {
		this.supportedAccountId = supportedAccountId;
	}

	private void sendJsonResponse(HttpExchange t, int statusCode, String response) {
		sendJsonResponse(t, statusCode, response.getBytes(UTF_8));
	}

	private void sendJsonResponse(HttpExchange t, int statusCode, byte[] response) {
		try {
			t.getResponseHeaders().add("Content-Type", "application/json");
			t.sendResponseHeaders(statusCode, response.length);

			OutputStream os = t.getResponseBody();
			os.write(response);

			os.close();
		} catch (Exception e) {
			LOG.error("FakeChattyPie - exception occurred when response", e);
		}
	}

	private String accountJson(String accountId) {
		return format("{\"id\": \"%s\", \"max_allowed_rooms\": 100}", accountId);
	}

	private String roomJson(String roomId, String accountId) {
		String roomName = "some-room-name";
		return format("{\"status\": \"active\", \"type\": \"standard\", \"id\": \"%s\", \"name\": \"%s\", \"account_id\": \"%s\"}", roomId, roomName, accountId);
	}
}
