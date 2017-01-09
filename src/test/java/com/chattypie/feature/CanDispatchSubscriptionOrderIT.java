package com.chattypie.feature;

import static com.chattypie.util.ITDatabaseUtils.readTestDatabaseUrl;
import static java.lang.System.setProperty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.util.SocketUtils.findAvailableTcpPort;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
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
import com.chattypie.support.fake.FakeAppmarket;
import com.chattypie.support.fake.FakeChattyPie;
import com.chattypie.support.fake.FakeEmailServer;
import com.chattypie.support.fake.WithFakeChattyPie;
import com.chattypie.support.fake.WithFakeEmailServer;
import com.chattypie.util.TestDatabaseHandle;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RootConfiguration.class, webEnvironment = RANDOM_PORT)
@ContextConfiguration(initializers = {WithFakeChattyPie.class, WithFakeEmailServer.class})
public class CanDispatchSubscriptionOrderIT {
	private static TestDatabaseHandle db;

	@LocalServerPort
	private int localConnectorPort;

	@Autowired
	private FakeChattyPie fakeChattyPie;
	@Autowired
	private FakeEmailServer fakeEmailServer;
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
	public void subscriptionOrderCreatesAccountAndRoomOnChattyPieAndSendsEmail() throws Exception {
		fakeChattyPie.setNextAccountId("the-account-id");

		HttpResponse response = fakeAppmarket.sendEventTo(connectorEventEndpoint(), "/v1/events/order");

		assertThat(response.getStatusLine().getStatusCode()).isEqualTo(202);
		assertThat(fakeAppmarket.allRequestPaths()).first().isEqualTo("/v1/events/order");
		assertThat(EntityUtils.toString(response.getEntity())).isEqualTo("{\"success\":true,\"message\":\"Event with eventId=order has been accepted by the connector. It will be processed soon.\"}");

		fakeAppmarket.waitForResolvedEvents(1);
		assertThat(fakeAppmarket.resolvedEvents()).contains("order");
		assertThat(fakeAppmarket.allRequestPaths()).last().isEqualTo("/api/integration/v1/events/order/result");
		assertThat(fakeAppmarket.lastRequestBody()).isEqualTo("{\"success\":true,\"message\":\"Successfully placed order for account: accountId = the-account-id, id = 6c9832df-a5f2-40dc-9fe6-823ac2c4f143\",\"accountIdentifier\":\"room-of-the-account-id\"}");

		assertThat(fakeChattyPie.allRequestPaths()).first().isEqualTo("POST /accounts");
		assertThat(fakeChattyPie.allRequestPaths()).last().isEqualTo("POST /accounts/the-account-id/rooms");

		assertThat(fakeEmailServer.lastMessageSubject()).isEqualTo("Welcome to Chatty Pie!");
	}

	private String connectorEventEndpoint() {
		return baseConnectorUrl() + "/api/v1/integration/processEvent";
	}

	private String baseConnectorUrl() {
		return "http://localhost:" + localConnectorPort;
	}
}
