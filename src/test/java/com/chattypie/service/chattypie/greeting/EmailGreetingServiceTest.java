package com.chattypie.service.chattypie.greeting;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.appdirect.sdk.appmarket.events.CompanyInfo;
import com.appdirect.sdk.notification.HtmlEmailNotificationService;
import com.appdirect.sdk.notification.SendNotificationFailedException;

@RunWith(MockitoJUnitRunner.class)
public class EmailGreetingServiceTest {

	private EmailGreetingService testedGreetingService;

	@Mock
	private HtmlEmailNotificationService mockEmailNotificationService;
	private String sampleGreetingSubject = "sample email subject";
	@Mock
	private EmailContentGenerator mockContentGenerator;

	@Before
	public void setUp() throws Exception {
		testedGreetingService = new EmailGreetingService(mockContentGenerator, mockEmailNotificationService, sampleGreetingSubject);
	}

	@Test
	public void sendNewCompanyGreeting_whenSendIsSuccessful_theGreetingIsSentAsEmail() throws Exception {
		//Given
		String expectedGreetingDestinationEmail = "newCompany@example.com";
		CompanyInfo testCompany = CompanyInfo.builder()
			.email(expectedGreetingDestinationEmail)
			.build();
		String expectedEmailContent = "Expected Email Content";
		when(mockContentGenerator.generateWelcomeEmail(anyString()))
			.thenReturn(expectedEmailContent);

		//When
		testedGreetingService.sendNewCompanyGreeting(testCompany);

		//Then
		verify(mockEmailNotificationService).sendHtmlEmail(
			sampleGreetingSubject,
			expectedEmailContent,
			testCompany.getEmail()
		);
	}

	@Test
	public void sendNewCompanyGreeting_whenNotificationFails_thenTheExceptionIsIgnored() throws Exception {
		//Given
		CompanyInfo testCompany = CompanyInfo.builder().build();

		doThrow(SendNotificationFailedException.class)
			.when(mockEmailNotificationService).sendHtmlEmail(anyString(), anyString(), anyString());

		//When
		testedGreetingService.sendNewCompanyGreeting(testCompany);

		//Then
		//...we complete without error
	}
}
