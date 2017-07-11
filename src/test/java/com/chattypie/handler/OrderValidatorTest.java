/*
 * Copyright 2017 AppDirect, Inc. and/or its affiliates
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
