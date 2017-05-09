package com.chattypie.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.appdirect.sdk.appmarket.events.OrderValidationStatus;
import com.appdirect.sdk.appmarket.validation.ValidationResponse;
import com.chattypie.LocalizedMessageService;

@RunWith(MockitoJUnitRunner.class)
public class OrderValidatorTest {

	@Mock
	private LocalizedMessageService localizedMessageService;

	private OrderValidator tested;

	@Before
	public void setUp() throws Exception {
		tested = new OrderValidator(localizedMessageService);
	}

	@Test
	public void testValidate_whenContactNameIsNotEmptyAndChatroomNameIsPresent_thenEmptyResultIsReturned() throws Exception {
		//Given
		Map<String, String> testSettings = new HashMap<>();
		testSettings.put("postalAddress.contactName", "John Smith");
		testSettings.put("chatroomName", "AChatroomName");

		//When
		ValidationResponse validationResponse = tested.validateOrderFields(testSettings);

		//Then
		assertThat(validationResponse.getResult()).isEmpty();
	}

	@Test
	public void testValidate_whenContactNameIsEmptyAndChatroomNameIsPresent_thenReturnAnErrorInResponse() throws Exception {
		//Given
		Map<String, String> testSettings = new HashMap<>();
		String nameOfIncorrectField = "postalAddress.contactName";
		testSettings.put(nameOfIncorrectField, "");
		testSettings.put("chatroomName", "AChatroomName");
		String testMessage = "testMessage";
		when(localizedMessageService.get(any()))
				.thenReturn(testMessage);

		OrderValidationStatus expectedValicationResponse = OrderValidationStatus.builder()
				.title(testMessage)
				.field(nameOfIncorrectField)
				.description(testMessage)
				.level("ERROR")
				.build();

		//When
		ValidationResponse validationResponse = tested.validateOrderFields(testSettings);

		//Then
		assertThat(validationResponse.getResult()).containsExactly(
			expectedValicationResponse
		);

		OrderValidationStatus validationMessage = validationResponse.getResult().iterator().next();
		assertThat(validationMessage.getField()).isEqualTo("postalAddress.contactName");
		assertThat(validationMessage.getLevel()).isEqualTo("ERROR");
	}
}
