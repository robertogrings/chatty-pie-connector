package com.chattypie.support.fake;

import static com.chattypie.support.JsonParser.readJson;
import static java.lang.String.format;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.beans.factory.DisposableBean;

import com.chattypie.service.chattypie.chatroom.Chatroom;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class FakeChattyPie implements DisposableBean {
	private final HttpServer server;
	private final List<String> allRequestPaths;
	private final List<String> allAccounts;
	private final List<Chatroom> allRooms;
	private String nextAccountId;

	static FakeChattyPie create(int port) throws ExecutionException, InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<HttpServer> futureServer = executor.submit(() -> HttpServer.create(new InetSocketAddress(port), 10));
		return new FakeChattyPie(futureServer.get());
	}

	private FakeChattyPie(HttpServer server) {
		this.server = server;
		this.allRequestPaths = new ArrayList<>();
		this.allAccounts = new ArrayList<>();
		this.allRooms = new ArrayList<>();
	}

	FakeChattyPie start() {
		server.createContext("/accounts", accountsRoute());
		server.createContext("/rooms", roomsRoute());

		server.start();
		return this;
	}

	@Override
	public void destroy() throws Exception {
		server.stop(0);
	}

	public void reset() {
		this.allRequestPaths.clear();
		this.allAccounts.clear();
		this.allRooms.clear();
	}

	int getPort() {
		return server.getAddress().getPort();
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
				FakeServerResponses.sendJsonResponse(httpExchange, 500, "{\"message\":\"FakeChattyPie - call setNextAccountId() before making calls to it.\"}");
				return;
			}

			allAccounts.add(nextAccountId);
			FakeServerResponses.sendJsonResponse(httpExchange, 201, accountJson(nextAccountId));
			nextAccountId = null;
		};

		final Consumer<HttpExchange> createRoom = httpExchange -> {
			String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
			String requestedAccountId = pathParts[2];
			if (!allAccounts.contains(requestedAccountId)) {
				FakeServerResponses.sendJsonResponse(httpExchange, 400, format("account %s does not exist", requestedAccountId));
				return;
			}
			String roomId = "room-of-" + requestedAccountId;
			FakeServerResponses.sendJsonResponse(httpExchange, 201, standardRoomJson(roomId, requestedAccountId));
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
				FakeServerResponses.sendJsonResponse(httpExchange, 400, format("room %s does not exist", requestedRoomId));
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
			FakeServerResponses.sendJsonResponse(httpExchange, 204, "");
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
				FakeServerResponses.methodNotAllowed(httpExchange);
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

	private void logRequest(HttpExchange httpExchange) {
		allRequestPaths.add(httpExchange.getRequestMethod() + " " + httpExchange.getRequestURI().toString());
	}
}
