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

package com.chattypie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@RunWith(MockitoJUnitRunner.class)
public class LocalizedMessageServiceImplTest {
	@Mock
	private MessageSource messageSourceMock;

	private LocalizedMessageServiceImpl tested;

	@Before
	public void setUp() throws Exception {
		tested = new LocalizedMessageServiceImpl(messageSourceMock);
	}

	@Test
	public void testGet_whenMessage() throws Exception {
		//Given
		Locale testLocale = Locale.US;
		String testMessageId = "test.message.id";
		String expectedLocalizedMessage = "Expected localized String";
		LocaleContextHolder.setLocale(testLocale);

		when(
				messageSourceMock.getMessage(
						eq(testMessageId),
						any(),
						eq(testLocale)
				)
		).thenReturn(expectedLocalizedMessage);

		//When
		String localizedMessage = tested.get(testMessageId);

		//Then
		assertThat(localizedMessage).isEqualTo(expectedLocalizedMessage);
	}
}
