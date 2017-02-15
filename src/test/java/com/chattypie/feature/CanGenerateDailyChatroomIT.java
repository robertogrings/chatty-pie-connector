package com.chattypie.feature;

import static com.chattypie.util.ITDatabaseUtils.readTestDatabaseUrl;
import static java.lang.System.setProperty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.apache.http.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.chattypie.RootConfiguration;
import com.chattypie.support.KubernetesCronAgent;
import com.chattypie.support.fake.WithFakeEmailServer;
import com.chattypie.util.TestDatabaseHandle;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RootConfiguration.class, webEnvironment = RANDOM_PORT)
@ContextConfiguration(initializers = {WithFakeEmailServer.class})
public class CanGenerateDailyChatroomIT {
	private static TestDatabaseHandle db;

	@LocalServerPort
	private int localConnectorPort;

	private KubernetesCronAgent cronAgent;

	@BeforeClass
	public static void beforeClass() throws Exception {
		db = new TestDatabaseHandle(readTestDatabaseUrl());
		setProperty("spring.datasource.url", db.getDatabaseUrl());
	}

	@Before
	public void setUp() throws Exception {
		cronAgent = new KubernetesCronAgent("very-secure", "password");
	}

	@After
	public void truncateDb() throws Exception {
		db.truncateSchema();
	}

	@Test
	public void acceptsOauthSignedRequest() throws Exception {
		HttpResponse httpResponse = cronAgent.getSecureEndpoint(connectorReportEndpoint());

		assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(200);
	}

	@Test
	@Ignore("This does not work yet. Connector currently accepts anonymous requests. Something for Emil to fix!")
	public void deniesAnonymousRequests() throws Exception {
		HttpResponse httpResponse = cronAgent.getAnonymousEndpoint(connectorReportEndpoint());

		assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(401);
	}

	private String connectorReportEndpoint() {
		return baseConnectorUrl() + "/chatrooms/daily";
	}

	private String baseConnectorUrl() {
		return "http://localhost:" + localConnectorPort;
	}
}
