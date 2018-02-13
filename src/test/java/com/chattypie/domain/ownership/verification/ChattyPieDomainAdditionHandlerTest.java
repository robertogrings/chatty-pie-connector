package com.chattypie.domain.ownership.verification;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ChattyPieDomainAdditionHandlerTest {

	@Mock
	private DomainOperationsService domainOperationsService;
	private ChattyPieDomainAdditionHandler chattyPieDomainAdditionHandler;

	@Before
	public void before() {
		chattyPieDomainAdditionHandler = new ChattyPieDomainAdditionHandler(domainOperationsService);
	}

	@Test
	public void testAddDomain_givenDomainAndAccountIdentifier_shouldCallService() {
		String customerId = UUID.randomUUID().toString();
		String domain = RandomStringUtils.randomAlphanumeric(8) + ".com";

		chattyPieDomainAdditionHandler.addDomain(customerId, domain);

		verify(domainOperationsService).addDomain(eq(customerId), eq(domain));
	}
}
