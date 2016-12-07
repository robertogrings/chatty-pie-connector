package com.chattypie.service.chattypie.greeting;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.Test;

import com.chattypie.persistence.model.ChatroomCreationRecord;
import com.google.common.collect.Lists;

public class EmailContentGeneratorTest {

	private EmailContentGenerator testedContentGenerator = new EmailContentGenerator();

	@Test
	public void testChatroomReportGeneration_whenTemplateArgumentsSupplied_theyAreSubstitutedInTemplate() throws Exception {
		//Given
		ZonedDateTime testTime = ZonedDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);
		ChatroomCreationRecord chatroom1 = new ChatroomCreationRecord(testTime.plusSeconds(1).toInstant(), "chatroom1");
		ChatroomCreationRecord chatroom2 = new ChatroomCreationRecord(testTime.plusSeconds(2).toInstant(), "chatroom2");
		ChatroomCreationRecord chatroom3 = new ChatroomCreationRecord(testTime.plusSeconds(3).toInstant(), "chatroom3");

		ZonedDateTime generationTime = testTime.plusSeconds(10);

		List<ChatroomCreationRecord> chatrooms = Lists.newArrayList(chatroom1, chatroom2, chatroom3);

		String expectedReport = "Date generated: 1970-01-01T00:00:10Z\n" +
			"\n" +
			"chatroom1: 1970-01-01T00:00:01Z\n" +
			"chatroom2: 1970-01-01T00:00:02Z\n" +
			"chatroom3: 1970-01-01T00:00:03Z\n";

		//When
		String report = testedContentGenerator.generateCreatedChatroomsReport(generationTime, chatrooms);

		//Then
		assertThat(report).isEqualTo(expectedReport);

	}

	@Test
	public void generateEmailContent_returnsAGreetingWithProperCompanyName() throws Exception {
		//Given
		String testCompanyName = "myCompanyName";
		String expectedEmailContent = "<HTML>\n" +
			"<HEAD>\n" +
			"  <TITLE>\n" +
			"    A Small Hello\n" +
			"  </TITLE>\n" +
			"</HEAD>\n" +
			"<BODY>\n" +
			"<H1>Hello myCompanyName </H1>\n" +
			"<P>This is very minimal \"hello world\" HTML document.</P>\n" +
			"</BODY>\n" +
			"</HTML>\n";

		//When
		String generatedEmailContent = testedContentGenerator.generateNewCompanyGreeting(testCompanyName);

		//Then
		assertThat(generatedEmailContent).isEqualTo(expectedEmailContent);
	}
}
