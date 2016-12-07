package com.chattypie.web;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chattypie.persistence.model.ChatroomCreationRecord;
import com.chattypie.service.chattypie.chatroom.ChatroomService;
import com.chattypie.service.chattypie.greeting.AsyncNotificationService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReportGenerationController {

	private final ChatroomService chatroomReportService;
	private final AsyncNotificationService notificationService;
	private final String receiverEmail;

	@RequestMapping(method = GET, value = "/chatrooms/weekly", produces = TEXT_PLAIN_VALUE)
	public String getChatroomReport() {
		List<ChatroomCreationRecord> chatroomsCreatedLastWeek = chatroomReportService.chatroomsCreatedLastWeek();
		notificationService.sendWeeklyChatroomCreatedReport(receiverEmail, chatroomsCreatedLastWeek);

		return "Report notification sent";
	}
}
