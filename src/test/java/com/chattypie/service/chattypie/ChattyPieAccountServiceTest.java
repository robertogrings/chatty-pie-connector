package com.chattypie.service.chattypie;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import com.chattypie.service.chattypie.account.ChattyPieAccount;
import com.chattypie.service.chattypie.account.ChattyPieAccountService;

@RunWith(MockitoJUnitRunner.class)
public class ChattyPieAccountServiceTest {

	private ChattyPieAccountService testedService;

	@Mock
	private RestTemplate mockRestTemplate;
	private String mockChattyPieHost = "http://www.example.com";

	@Before
	public void setUp() throws Exception {
		testedService = new ChattyPieAccountService(mockRestTemplate, mockChattyPieHost);
	}

	@Test
	public void testCreateChattyPieAccount_whenRestCallToChattyPieSucceeds_thenTheResponseOfTheRestCallIsPassedToCaller() throws Exception {
		//Given
		int expectedMaxRooms = 100;
		ChattyPieAccount expectedChattyPieAccount = new ChattyPieAccount("someId", expectedMaxRooms);
		when(
			mockRestTemplate.postForObject(
				format("%s/accounts", mockChattyPieHost),
				format("{\"max_allowed_rooms\": %d}", expectedMaxRooms),
				ChattyPieAccount.class
			)
		).thenReturn(expectedChattyPieAccount);

		//When
		ChattyPieAccount chattyPieAccountCreated = testedService.createChattyPieAccount();

		//Then
		assertThat(chattyPieAccountCreated).isEqualTo(expectedChattyPieAccount);
	}

}
