package com.chattypie.service.chattypie.greeting;

import static java.lang.String.format;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.appdirect.sdk.appmarket.events.CompanyInfo;
import com.appdirect.sdk.notification.HtmlEmailNotificationService;

@Slf4j
@RequiredArgsConstructor
public class EmailGreetingService implements GreetingService {
	private final EmailContentGenerator emailContentGenerator;
	private final HtmlEmailNotificationService emailNotificationService;
	private final String greetingEmailSubject;

	@Override
	public void sendNewCompanyGreeting(CompanyInfo company) {
		try {
			String emailBody = emailContentGenerator.generateWelcomeEmail(company.getName());
			emailNotificationService.sendHtmlEmail(greetingEmailSubject, emailBody, company.getEmail());
		} catch (Exception e) {
			log.warn(
				format(
					"Failed sending a welcome notification to email=%s", 
					company.getEmail()
				), 
				e
			);
		}
	}
}
