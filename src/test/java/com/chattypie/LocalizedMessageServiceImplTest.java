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
