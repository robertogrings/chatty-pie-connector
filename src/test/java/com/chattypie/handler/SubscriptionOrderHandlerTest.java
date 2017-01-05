package com.chattypie.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.appdirect.sdk.appmarket.events.APIResult;
import com.appdirect.sdk.appmarket.events.CompanyInfo;
import com.appdirect.sdk.appmarket.events.SubscriptionOrder;
import com.chattypie.persistence.model.CompanyAccount;
import com.chattypie.service.appmarket.CompanyAccountService;
import com.chattypie.service.chattypie.chatroom.Chatroom;
import com.chattypie.service.chattypie.chatroom.ChatroomService;
import com.chattypie.service.chattypie.greeting.NotificationService;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionOrderHandlerTest {

	private SubscriptionOrderHandler testedSubscriptionOrderHandler;

	@Mock
	private ChatroomService mockChatroomService;

	@Mock
	private CompanyAccountService mockCompanyAccountService;

	@Mock
	private NotificationService mockNotificationService;

	@Before
	public void setUp() throws Exception {
		testedSubscriptionOrderHandler = new SubscriptionOrderHandler(
				mockCompanyAccountService,
				mockChatroomService,
				mockNotificationService);
	}

	@Test
	public void testProcess_whenCompanyDoesNotHaveAnAccount_thenCreateAccountAndAddAChatroom() throws Exception {
		//Given
		String expectedNewChatroomName = "chatroomName";
		String testCompanyUuid = "testUuid";
		SubscriptionOrder testEvent = newOrderEvent(expectedNewChatroomName, testCompanyUuid);

		when(mockCompanyAccountService.findExistingCompanyAccountById(testCompanyUuid))
			.thenReturn(Optional.empty());

		String expectedAccountId = "sampleId";
		when(mockCompanyAccountService.createCompanyAccountFor(testCompanyUuid))
			.thenReturn(new CompanyAccount(expectedAccountId, testCompanyUuid));

		String newChatroomId = "chatroomId";
		when(mockChatroomService.createChatroomForAccount(expectedAccountId, expectedNewChatroomName))
			.thenReturn(Chatroom.builder().id(newChatroomId).name(expectedNewChatroomName).accountId(expectedAccountId).build());

		//When
		APIResult result = testedSubscriptionOrderHandler.handle(testEvent);

		//Then
		assertThat(result.isSuccess())
			.as("The result is successful")
			.isTrue();
		verify(mockChatroomService)
			.createChatroomForAccount(eq(expectedAccountId), eq(expectedNewChatroomName));
		assertThat(result.getAccountIdentifier()).isEqualTo(newChatroomId);
	}

	@Test
	public void testProcess_whenCompanyDoesHaveAnAccount_thenAddAChatroomToExistingAccount() throws Exception {
		//Given
		String expectedNewChatroomName = "chatroomName";
		String testCompanyUuid = "testUuid";
		SubscriptionOrder testEvent = newOrderEvent(expectedNewChatroomName, testCompanyUuid);

		String existingAccountId = "existingAccountId";
		when(mockCompanyAccountService.findExistingCompanyAccountById(testCompanyUuid))
			.thenReturn(Optional.of(new CompanyAccount(existingAccountId, testCompanyUuid)));

		String newChatroomId = "chatroomId";
		when(mockChatroomService.createChatroomForAccount(existingAccountId, expectedNewChatroomName))
			.thenReturn(Chatroom.builder().id(newChatroomId).name(expectedNewChatroomName).accountId(existingAccountId).build());

		//When
		APIResult result = testedSubscriptionOrderHandler.handle(testEvent);

		//Then
		assertThat(result.isSuccess())
			.as("The result is successful")
			.isTrue();
		verify(mockChatroomService)
			.createChatroomForAccount(eq(existingAccountId), eq(expectedNewChatroomName));
		assertThat(result.getAccountIdentifier()).isEqualTo(newChatroomId);
	}

	private SubscriptionOrder newOrderEvent(String chatroomName, String companyUuid) {
		return new SubscriptionOrder(
				"some-key",
				null,
				null,
				config("chatroomName", chatroomName),
				CompanyInfo.builder()
						.uuid(companyUuid)
						.build(),
				null,
				null,
				null,
				null
		);
	}

	private Map<String, String> config(String... keyValues) {
		Map<String, String> config = new HashMap<>();
		for (int i = 0; i < keyValues.length; i++) {
			config.put(keyValues[i], keyValues[++i]);
		}
		return config;
	}
}
