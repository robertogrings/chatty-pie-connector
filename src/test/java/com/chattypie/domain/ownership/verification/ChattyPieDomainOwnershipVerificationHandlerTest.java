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

import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ChattyPieDomainOwnershipVerificationHandlerTest {

	private ChattyPieDomainOwnershipVerificationHandler tested;

	@Mock
	private ChattyPieDomainOwnershipVerificationHandlerWithCallback innerHandlerMock;

	@Before
	public void setUp() throws Exception {
		tested = new ChattyPieDomainOwnershipVerificationHandler(innerHandlerMock);
	}

	@Test
	public void testVefifyDomainOwnership_whenCalled_thenItForwardsToInner() throws Exception {
		//Given
		String testChatroomId = UUID.randomUUID().toString();
		String testDomain = "example.com";
		String testCallbackUrl = "http://amarketplace.appdirect.com";
		String testKey = UUID.randomUUID().toString();

		//When
		tested.verifyDomainOwnership(testChatroomId, testDomain, testCallbackUrl, testKey);

		//Then
		verify(innerHandlerMock).verifyWithRetries(testChatroomId, testDomain, testCallbackUrl, testKey);
	}
}
