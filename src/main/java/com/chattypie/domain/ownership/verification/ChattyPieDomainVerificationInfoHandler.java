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

import lombok.RequiredArgsConstructor;

import com.appdirect.sdk.appmarket.domain.DnsOwnershipVerificationRecords;
import com.appdirect.sdk.appmarket.domain.DomainDnsVerificationInfoHandler;
import com.appdirect.sdk.appmarket.domain.TxtDnsRecord;
import com.chattypie.service.chattypie.chatroom.ChatroomService;
import com.google.common.collect.Sets;

@RequiredArgsConstructor
public class ChattyPieDomainVerificationInfoHandler implements DomainDnsVerificationInfoHandler {

	private final ChatroomService chatroomService;
	private final DomainOperationsService domainOperationsService;

	@Override
	public DnsOwnershipVerificationRecords readOwnershipVerificationRecords(String chatroomId, String domain) {

		ChattyPieDomainOwnershipProof ownershipProof = domainOperationsService.getDomainOwnershipProof(
				chatroomService.readIdOfAccountAssociatedWith(chatroomId),
				domain
		);
		final TxtDnsRecord verificationRecord = new TxtDnsRecord("@", 3600, ownershipProof.getToken());

		return new DnsOwnershipVerificationRecords(
				Sets.newHashSet(verificationRecord),
				Sets.newHashSet()
		);
	}
}
