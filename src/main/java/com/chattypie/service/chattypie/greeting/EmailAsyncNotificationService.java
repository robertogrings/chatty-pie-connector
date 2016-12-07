package com.chattypie.service.chattypie.greeting;

import static java.lang.String.format;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.appdirect.sdk.appmarket.events.CompanyInfo;
import com.appdirect.sdk.notification.HtmlEmailNotificationService;
import com.chattypie.persistence.model.ChatroomCreationRecord;

@Slf4j
@RequiredArgsConstructor
public class EmailAsyncNotificationService implements AsyncNotificationService {
	static final String WELCOME_EMAIL_SUBJECT = "Welcome to Chatty Pie!";

	private final ExecutorService executorService;
	private final EmailContentGenerator emailContentGenerator;
	private final HtmlEmailNotificationService emailNotificationService;

	@Override
	public void sendNewCompanyGreeting(CompanyInfo company) {
		String emailBody = emailContentGenerator.generateNewCompanyGreeting(company.getName());
		emailNotificationService.sendHtmlEmail(WELCOME_EMAIL_SUBJECT, emailBody, company.getEmail());
	}

	@Override
	public void sendWeeklyChatroomCreatedReport(String subscriberEmail, List<ChatroomCreationRecord> chatroomsCreated) {
		ZonedDateTime reportGenerationDate = ZonedDateTime.now();
		String reportEmail = emailContentGenerator.generateCreatedChatroomsReport(reportGenerationDate, chatroomsCreated);
		emailNotificationService.sendHtmlEmail(WELCOME_EMAIL_SUBJECT, reportEmail, subscriberEmail);
	}
}
