package com.chattypie.service.chattypie.greeting;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.appdirect.sdk.appmarket.events.CompanyInfo;
import com.appdirect.sdk.notification.HtmlEmailNotificationService;
import com.chattypie.persistence.model.ChatroomCreationRecord;

@Slf4j
@RequiredArgsConstructor
public class EmailNotificationService implements NotificationService {
	static final String WELCOME_EMAIL_SUBJECT = "Welcome to Chatty Pie!";
	static final String CHATROOMS_CREATED_REPORT_SUBJECT = "Chatty Pie Daily Chatrooms Created Report";

	private final EmailContentGenerator emailContentGenerator;
	private final HtmlEmailNotificationService htmlEmailNotificationService;

	@Override
	public void sendNewCompanyGreeting(CompanyInfo company) {
		String emailBody = emailContentGenerator.generateNewCompanyGreeting(company.getName());
		htmlEmailNotificationService.sendHtmlEmail(WELCOME_EMAIL_SUBJECT, emailBody, company.getEmail());
	}

	@Override
	public void sendDailyChatroomCreatedReport(String subscriberEmail, List<ChatroomCreationRecord> chatroomsCreated) {
		ZonedDateTime reportGenerationDate = ZonedDateTime.now();
		String reportEmail = emailContentGenerator.generateCreatedChatroomsReport(reportGenerationDate, chatroomsCreated);
		htmlEmailNotificationService.sendHtmlEmail(CHATROOMS_CREATED_REPORT_SUBJECT, reportEmail, subscriberEmail);
	}
}
