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

package com.chattypie.domain.ownership.verification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.appdirect.sdk.appmarket.domain.DnsOwnershipVerificationRecords;
import com.appdirect.sdk.appmarket.domain.TxtDnsRecord;
import com.chattypie.service.chattypie.chatroom.Chatroom;
import com.chattypie.service.chattypie.chatroom.ChatroomService;
import com.google.common.collect.Sets;

@RunWith(MockitoJUnitRunner.class)
public class ChattyPieDomainVerificationInfoHandlerTest {

	private ChattyPieDomainVerificationInfoHandler chattyPieDomainVerificationInfoHandler;

	@Mock
	private DomainOperationsService domainOperationsServiceMock;
	@Mock
	private ChatroomService chatroomServiceMock;

	@Before
	public void setUp() throws Exception {
		chattyPieDomainVerificationInfoHandler = new ChattyPieDomainVerificationInfoHandler(chatroomServiceMock, domainOperationsServiceMock);
	}

	@Test
	public void testReadOwnershipVerificationRecords_whenCalled_thenTheAccountIsResolvedByChattyPieAndDomainOwhershipRecordsAreFetched() throws Exception {
		//Given
		String testChatroomId = "abc123";
		String testDomainName = "example.com";
		final Chatroom expectedChatroom = testChatroom(testChatroomId);
		final ChattyPieDomainOwnershipProof expectedOwnershipProof = expectedChattyPieDomainOwnershipProof(
				expectedChatroom.getAccountId(),
				testDomainName
		);
		final DnsOwnershipVerificationRecords expectedDnsOwnershipVerificationRecords = expectedDnsVerificationRecords(expectedOwnershipProof.getToken());

		when(
				chatroomServiceMock.readIdOfAccountAssociatedWith(testChatroomId)
		).thenReturn(
				expectedChatroom.getAccountId()
		);

		when(
				domainOperationsServiceMock.getDomainOwnershipProof(expectedChatroom.getAccountId(), testDomainName)
		).thenReturn(
				expectedOwnershipProof
		);

		//When
		final DnsOwnershipVerificationRecords actualDnsOwnershipVerificationRecords = chattyPieDomainVerificationInfoHandler.readOwnershipVerificationRecords(testChatroomId, testDomainName);

		//Then
		assertThat(actualDnsOwnershipVerificationRecords).isEqualTo(expectedDnsOwnershipVerificationRecords);
	}

	private DnsOwnershipVerificationRecords expectedDnsVerificationRecords(String token) {
		final TxtDnsRecord txtDnsRecord = new TxtDnsRecord("@", 3600, token);
		return new DnsOwnershipVerificationRecords(Sets.newHashSet(txtDnsRecord), new HashSet<>());
	}

	private ChattyPieDomainOwnershipProof expectedChattyPieDomainOwnershipProof(String accountId, String testDomainName) {
		return ChattyPieDomainOwnershipProof.builder()
				.account(accountId)
				.domain(testDomainName)
				.recordType("TXT")
				.token(UUID.randomUUID().toString())
				.build();
	}

	private Chatroom testChatroom(String testChatroomId) {
		return Chatroom.builder()
				.id(testChatroomId)
				.name("A Dummy Name")
				.type("standard")
				.status("active")
				.fullHistoryEnabled(false)
				.build();
	}
}
