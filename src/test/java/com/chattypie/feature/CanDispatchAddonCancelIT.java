package com.chattypie.feature;

import static com.chattypie.util.ITDatabaseUtils.readTestDatabaseUrl;
import static java.lang.System.setProperty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.util.SocketUtils.findAvailableTcpPort;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.chattypie.RootConfiguration;
import com.chattypie.service.chattypie.chatroom.Chatroom;
import com.chattypie.support.fake.FakeAppmarket;
import com.chattypie.support.fake.FakeChattyPie;
import com.chattypie.support.fake.WithFakeChattyPie;
import com.chattypie.support.fake.WithFakeEmailServer;
import com.chattypie.util.TestDatabaseHandle;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RootConfiguration.class, webEnvironment = RANDOM_PORT)
@ContextConfiguration(initializers = {WithFakeChattyPie.class, WithFakeEmailServer.class})
public class CanDispatchAddonCancelIT {
	private static TestDatabaseHandle db;

	@LocalServerPort
	private int localConnectorPort;

	@Autowired
	private FakeChattyPie fakeChattyPie;
	private FakeAppmarket fakeAppmarket;

	@BeforeClass
	public static void beforeClass() throws Exception {
		db = new TestDatabaseHandle(readTestDatabaseUrl());
		setProperty("spring.datasource.url", db.getDatabaseUrl());
	}

	@Before
	public void setUp() throws Exception {
		fakeChattyPie.reset();
		fakeAppmarket = FakeAppmarket.create(findAvailableTcpPort(), "very-secure", "password").start();
	}

	@After
	public void stopAppMarketAndTruncateDb() throws Exception {
		fakeAppmarket.stop();
		db.truncateSchema();
	}

	@Test
	public void addonCancel_DisablesUnlimitedHistoryOnChatroom() throws Exception {
		fakeChattyPie.setNextAccountId("the-account");
		fakeChattyPie.addOrUpdateRoom(standardRoomWithUnlimitedHistory("the-account", "room-of-the-account"));

		fakeAppmarket.sendEventTo(connectorEventEndpoint(), "/v1/events/cancel-addon");
		fakeAppmarket.waitForResolvedEvents(1);
		assertThat(fakeAppmarket.resolvedEvents()).contains("cancel-addon");

		assertThat(fakeAppmarket.lastRequestBody()).isEqualTo("{\"success\":true,\"message\":\"Addon account with accountId=9d6fca98-aa94-462b-85fa-118804ad3fe3 cancelled successfully\"}");

		assertThat(fakeChattyPie.allRequestPaths()).last().isEqualTo("PUT /rooms/room-of-the-account");
		assertThat(fakeChattyPie.getExistingRoom("room-of-the-account").isFullHistoryEnabled()).isFalse();
	}

	private Chatroom standardRoomWithUnlimitedHistory(String accountId, String roomId) {
		return Chatroom.builder().accountId(accountId)
				.id(roomId)
				.type("standard")
				.status("active")
				.fullHistoryEnabled(true)
				.build();
	}

	private String connectorEventEndpoint() {
		return baseConnectorUrl() + "/api/v1/integration/processEvent";
	}

	private String baseConnectorUrl() {
		return "http://localhost:" + localConnectorPort;
	}
}
