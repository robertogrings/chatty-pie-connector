package com.chattypie.feature;

import static com.chattypie.util.ITDatabaseUtils.readTestDatabaseUrl;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.chattypie.ChattyPieConnectorApplication;
import com.chattypie.service.chattypie.chatroom.Chatroom;
import com.chattypie.support.FakeAppmarket;
import com.chattypie.support.FakeChattyPie;
import com.chattypie.support.FakeEmailServer;
import com.chattypie.util.TestDatabaseHandle;

public class CanDispatchAddonOrderIT {
	private int localConnectorPort = 3453;
	private FakeAppmarket fakeAppmarket;
	private FakeChattyPie fakeChattyPie;
	private FakeEmailServer fakeEmailServer;
	private ChattyPieConnectorApplication connector;
	private TestDatabaseHandle db;

	@Before
	public void setUp() throws Exception {
		fakeAppmarket = FakeAppmarket.create(localConnectorPort + 1, "very-secure", "password").start();
		fakeChattyPie = FakeChattyPie.create(localConnectorPort + 2).start();
		fakeEmailServer = FakeEmailServer.create(localConnectorPort + 3).start();
		db = new TestDatabaseHandle(readTestDatabaseUrl());

		connector = new ChattyPieConnectorApplication().start(
				"--server.port=" + localConnectorPort,
				"--chatty.pie.host=http://localhost:" + (localConnectorPort + 2),
				"--spring.datasource.url=" + db.getDatabaseUrl(),
				"--spring.mail.port=" + (localConnectorPort + 3)
		);
	}

	@After
	public void stop() throws Exception {
		connector.stop();
		fakeEmailServer.stop();
		fakeChattyPie.stop();
		fakeAppmarket.stop();
		db.truncateSchema();
	}

	@Test
	public void addonOrderEnablesUnlimitedHistoryOnChatroom() throws Exception {
		fakeChattyPie.setNextAccountId("the-account");
		fakeChattyPie.addOrUpdateRoom(standardRoomWithLimitedHistory("the-account", "room-of-the-account"));

		fakeAppmarket.sendEventTo(connectorEventEndpoint(), "/v1/events/order-addon");
		fakeAppmarket.waitForResolvedEvents(1);
		assertThat(fakeAppmarket.resolvedEvents()).contains("order-addon");

		assertThat(fakeAppmarket.lastRequestBody()).isEqualTo("{\"success\":true,\"message\":\"Successfully purchased add-on\",\"accountIdentifier\":\"unlimited-history-room-of-the-account\"}");

		assertThat(fakeChattyPie.allRequestPaths()).last().isEqualTo("PUT /rooms/room-of-the-account");
		assertThat(fakeChattyPie.getExistingRoom("room-of-the-account").isFullHistoryEnabled()).isTrue();
	}

	private Chatroom standardRoomWithLimitedHistory(String accountId, String roomId) {
		return Chatroom.builder().accountId(accountId)
				.id(roomId)
				.type("standard")
				.status("active")
				.fullHistoryEnabled(false)
				.build();
	}

	private String connectorEventEndpoint() {
		return baseConnectorUrl() + "/api/v1/integration/processEvent";
	}

	private String baseConnectorUrl() {
		return "http://localhost:" + localConnectorPort;
	}
}
