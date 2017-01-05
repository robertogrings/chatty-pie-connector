package com.chattypie.feature;

import static com.chattypie.util.ITDatabaseUtils.readTestDatabaseUrl;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.chattypie.ChattyPieConnectorApplication;
import com.chattypie.support.FakeAppmarket;
import com.chattypie.support.FakeChattyPie;
import com.chattypie.support.FakeEmailServer;
import com.chattypie.util.TestDatabaseHandle;

public class CanDispatchSubscriptionOrderIT {
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
	public void subscriptionOrderCreatesAccountAndRoomOnChattyPieAndSendsEmail() throws Exception {
		fakeChattyPie.setSupportedAccountId("the-account-id");

		HttpResponse response = fakeAppmarket.sendEventTo(connectorEventEndpoint(), "/v1/events/order");

		assertThat(response.getStatusLine().getStatusCode()).isEqualTo(202);
		assertThat(fakeAppmarket.allRequestPaths()).first().isEqualTo("/v1/events/order");
		assertThat(EntityUtils.toString(response.getEntity())).isEqualTo("{\"success\":true,\"message\":\"Event with eventId=order has been accepted by the connector. It will be processed soon.\"}");

		fakeAppmarket.waitForResolvedEvents(1);
		assertThat(fakeAppmarket.resolvedEvents()).contains("order");
		assertThat(fakeAppmarket.allRequestPaths()).last().isEqualTo("/api/integration/v1/events/order/result");
		assertThat(fakeAppmarket.lastRequestBody()).isEqualTo("{\"success\":true,\"message\":\"Successfully placed order for account: accountId = the-account-id, id = 6c9832df-a5f2-40dc-9fe6-823ac2c4f143\",\"accountIdentifier\":\"room-of-the-account-id\"}");

		assertThat(fakeChattyPie.allRequestPaths()).first().isEqualTo("/accounts");
		assertThat(fakeChattyPie.allRequestPaths()).last().isEqualTo("/accounts/the-account-id/rooms");

		assertThat(fakeEmailServer.lastMessageSubject()).isEqualTo("Welcome to Chatty Pie!");
	}

	private String connectorEventEndpoint() {
		return baseConnectorUrl() + "/api/v1/integration/processEvent";
	}

	private String baseConnectorUrl() {
		return "http://localhost:" + localConnectorPort;
	}
}
