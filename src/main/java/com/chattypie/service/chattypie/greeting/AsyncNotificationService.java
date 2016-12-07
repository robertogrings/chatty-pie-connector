package com.chattypie.service.chattypie.greeting;

import java.util.List;

import com.appdirect.sdk.appmarket.events.CompanyInfo;
import com.chattypie.persistence.model.ChatroomCreationRecord;

public interface AsyncNotificationService {
	void sendNewCompanyGreeting(CompanyInfo company);

	void sendWeeklyChatroomCreatedReport(String subscriberEmail, List<ChatroomCreationRecord> chatroomsCreated);
}
