package com.chattypie.support;

import static com.chattypie.support.FakeServerResponses.methodNotAllowed;
import static com.chattypie.support.FakeServerResponses.sendJsonResponse;
import static com.chattypie.support.JsonParser.readJson;
import static java.lang.String.format;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chattypie.service.chattypie.chatroom.Chatroom;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class FakeChattyPie {
	private final static Logger LOG = LoggerFactory.getLogger(FakeChattyPie.class);

	private final HttpServer server;
	private final List<String> allRequestPaths;
	private final List<String> allAccounts;
	private final List<Chatroom> allRooms;
	private String nextAccountId;

	public static FakeChattyPie create(int port) throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		return new FakeChattyPie(server);
	}

	private FakeChattyPie(HttpServer server) {
		this.server = server;
		this.allRequestPaths = new ArrayList<>();
		this.allAccounts = new ArrayList<>();
		this.allRooms = new ArrayList<>();
	}

	public FakeChattyPie start() {
		server.createContext("/accounts", accountsRoute());
		server.createContext("/rooms", roomsRoute());

		server.start();
		return this;
	}

	public void stop() {
		server.stop(0);
	}

	public List<String> allRequestPaths() {
		return new ArrayList<>(allRequestPaths);
	}

	public void setNextAccountId(String nextAccountId) {
		this.nextAccountId = nextAccountId;
	}

	public void addOrUpdateRoom(Chatroom roomToAdd) {
		tryGetExistingRoom(roomToAdd.getId()).ifPresent(allRooms::remove);
		allRooms.add(roomToAdd);
	}

	public Chatroom getExistingRoom(String roomId) {
		return tryGetExistingRoom(roomId).orElseThrow(() -> new IllegalArgumentException("room #" + roomId + " does not exist on this fake chatty-pie"));
	}

	private Optional<Chatroom> tryGetExistingRoom(String roomId) {
		return allRooms.stream().filter(r -> r.getId().equals(roomId)).findFirst();
	}

	private HttpHandler accountsRoute() {
		final Consumer<HttpExchange> createAccount = httpExchange -> {
			if (nextAccountId == null) {
				sendJsonResponse(httpExchange, 500, "{\"message\":\"FakeChattyPie - call setNextAccountId() before making calls to it.\"}");
				return;
			}

			allAccounts.add(nextAccountId);
			sendJsonResponse(httpExchange, 201, accountJson(nextAccountId));
			nextAccountId = null;
		};

		final Consumer<HttpExchange> createRoom = httpExchange -> {
			String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
			String requestedAccountId = pathParts[2];
			if (!allAccounts.contains(requestedAccountId)) {
				sendJsonResponse(httpExchange, 400, format("account %s does not exist", requestedAccountId));
				return;
			}
			String roomId = "room-of-" + requestedAccountId;
			sendJsonResponse(httpExchange, 201, standardRoomJson(roomId, requestedAccountId));
			addOrUpdateRoom(newStandardRoom(requestedAccountId, roomId));
		};

		return logAndHandleRequest(httpExchange -> {
			String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
			String lastPathPart = pathParts[pathParts.length - 1];
			String requestMethod = httpExchange.getRequestMethod();
			if ("POST".equals(requestMethod)) {
				if (lastPathPart.equals("accounts")) {
					createAccount.accept(httpExchange);
				} else {
					createRoom.accept(httpExchange);
				}
				return true;
			}
			return false;
		});
	}

	private HttpHandler roomsRoute() {
		Consumer<HttpExchange> updateRoom = httpExchange -> {
			String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
			String requestedRoomId = pathParts[2];
			Chatroom requestedRoom = tryGetExistingRoom(requestedRoomId).orElse(null);
			if (requestedRoom == null) {
				sendJsonResponse(httpExchange, 400, format("room %s does not exist", requestedRoomId));
				return;
			}

			Chatroom.ChatroomBuilder updatedRoomBuilder = chatroomBuilderFrom(requestedRoom);
			JsonNode jsonRequestBody = readJson(httpExchange.getRequestBody());

			if (jsonRequestBody.get("status") != null) {
				updatedRoomBuilder.status(jsonRequestBody.get("status").asText());
			}

			if (jsonRequestBody.get("type") != null) {
				updatedRoomBuilder.type(jsonRequestBody.get("type").asText());
			}

			if (jsonRequestBody.get("full_history_enabled") != null) {
				updatedRoomBuilder.fullHistoryEnabled(jsonRequestBody.get("full_history_enabled").asBoolean());
			}

			addOrUpdateRoom(updatedRoomBuilder.build());
			sendJsonResponse(httpExchange, 204, "");
		};

		return logAndHandleRequest(httpExchange -> {
			if ("PUT".equals(httpExchange.getRequestMethod())) {
				updateRoom.accept(httpExchange);
				return true;
			}
			return false;
		});
	}

	private HttpHandler logAndHandleRequest(Function<HttpExchange, Boolean> requestHandler) {
		return httpExchange -> {
			logRequest(httpExchange);

			if (!requestHandler.apply(httpExchange)) {
				methodNotAllowed(httpExchange);
			}
		};
	}

	private String accountJson(String accountId) {
		return format("{\"id\": \"%s\", \"max_allowed_rooms\": 100}", accountId);
	}

	private String standardRoomJson(String roomId, String accountId) {
		return format("{\"status\": \"active\", \"type\": \"standard\", \"id\": \"%s\", \"name\": \"some-room-name\", \"account_id\": \"%s\"}", roomId, accountId);
	}

	private Chatroom.ChatroomBuilder chatroomBuilderFrom(Chatroom originalRoom) {
		return Chatroom.builder()
				.accountId(originalRoom.getAccountId())
				.id(originalRoom.getId())
				.name(originalRoom.getName())
				.status(originalRoom.getStatus())
				.type(originalRoom.getType())
				.fullHistoryEnabled(originalRoom.isFullHistoryEnabled());
	}

	private Chatroom newStandardRoom(String accountId, String roomId) {
		return Chatroom.builder().id(roomId).name("some-room-name").type("standard").status("active").accountId(accountId).build();
	}

	private boolean logRequest(HttpExchange httpExchange) {
		return allRequestPaths.add(httpExchange.getRequestMethod() + " " + httpExchange.getRequestURI().toString());
	}
}
