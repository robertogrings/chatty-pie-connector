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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.appdirect.sdk.appmarket.domain.DomainVerificationNotificationClient;
import com.chattypie.service.chattypie.chatroom.ChatroomService;

@RunWith(MockitoJUnitRunner.class)
public class ChattyPieDomainOwnershipVerificationHandlerWithCallbackTest {

	private ChattyPieDomainOwnershipVerificationHandlerWithCallback tested;

	@Mock
	private DomainOperationsService domainOperationsServiceMock;

	@Mock
	private ChatroomService chatroomServiceMock;

	@Mock
	private DomainVerificationNotificationClient domainVerificationNotificationClientMock;

	@Before
	public void setUp() throws Exception {
		tested = new ChattyPieDomainOwnershipVerificationHandlerWithCallback(chatroomServiceMock,
				domainOperationsServiceMock,
				domainVerificationNotificationClientMock,
				retryTemplate(3)
		);
	}

	@Test
	public void testVerifyDomainOwnership_whenSuccessful_successNotificationIsSentToTheMarketplace() throws Exception {
		//Given
		String testChatroomId = UUID.randomUUID().toString();
		String testDomain = "example.com";
		String testCallbackUrl = "http://amarketplace.appdirect.com";
		String testKey = UUID.randomUUID().toString();
		String expectedAccountId = UUID.randomUUID().toString();

		when(
				chatroomServiceMock.readIdOfAccountAssociatedWith(testChatroomId)
		).thenReturn(
				expectedAccountId
		);

		//When
		final Future<Void> voidFuture = tested.verifyWithRetries(testChatroomId, testDomain, testCallbackUrl, testKey);
		voidFuture.get();

		//Then
		verify(domainOperationsServiceMock).triggerDomainOwnerhshipVerification(
				expectedAccountId,
				testDomain
		);
		verify(domainVerificationNotificationClientMock).resolveDomainVerification(testCallbackUrl, testKey, true);
	}

	@Test
	public void testVerifyDomainOwnership_whenCalled_verificationOfTheAccountAssociatedWithTheChatroomIsTriggered() throws Exception {
		//Given
		String testChatroomId = UUID.randomUUID().toString();
		String testDomain = "example.com";
		String testCallbackUrl = "http://amarketplace.appdirect.com";
		String testKey = UUID.randomUUID().toString();
		String expectedAccountId = UUID.randomUUID().toString();

		when(
				chatroomServiceMock.readIdOfAccountAssociatedWith(testChatroomId)
		).thenReturn(
				expectedAccountId
		);

		doThrow(RuntimeException.class)
				.when(domainOperationsServiceMock)
				.triggerDomainOwnerhshipVerification(any(), any());

		//When
		final Future<Void> voidFuture = tested.verifyWithRetries(testChatroomId, testDomain, testCallbackUrl, testKey);
		voidFuture.get();

		//Then
		verify(domainOperationsServiceMock, times(3)).triggerDomainOwnerhshipVerification(
				expectedAccountId,
				testDomain
		);
		verify(domainVerificationNotificationClientMock).resolveDomainVerification(testCallbackUrl, testKey, false);
	}

	public RetryTemplate retryTemplate(int maxRetries) {
		final RetryTemplate retryTemplate = new RetryTemplate();
		FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
		fixedBackOffPolicy.setBackOffPeriod(1l);
		retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

		SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
		retryPolicy.setMaxAttempts(maxRetries);
		retryTemplate.setRetryPolicy(retryPolicy);

		return retryTemplate;
	}
}
