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

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import com.chattypie.domain.ownership.verification.exception.DomainAlreadyExistsException;
import com.chattypie.domain.ownership.verification.exception.DomainNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class DomainOperationsServiceTest {

	private DomainOperationsService tested;
	private String chattyPieHostMock = "http://example.com";

	@Mock
	private RestTemplate restTemplateMock;

	@Before
	public void setUp() throws Exception {
		tested = new DomainOperationsService(restTemplateMock, chattyPieHostMock);
	}

	@Test
	public void testGetDomainOwnershipProof_whenCalled_thenAGetShouldBeInvokedOnTheChattyPieOwnershipProofEndpoint() {
		//Given
		String testDomain = "dummy.com";
		String testAccountId = UUID.randomUUID().toString();
		ChattyPieDomainOwnershipProof expectedOwnershipProof = ChattyPieDomainOwnershipProof.builder()
				.build();
		final String expectedGetUrl = format("%s/accounts/%s/domains/%s/ownershipProof", chattyPieHostMock, testAccountId, testDomain);

		when(
				restTemplateMock.getForObject(
						expectedGetUrl,
						ChattyPieDomainOwnershipProof.class
				)
		).thenReturn(
				expectedOwnershipProof
		);

		//When
		final ChattyPieDomainOwnershipProof actualDomainOwnershipProof = tested.getDomainOwnershipProof(testAccountId, testDomain);

		//Then
		assertThat(actualDomainOwnershipProof).isEqualTo(expectedOwnershipProof);
	}

	@Test
	public void testAddDomain_givenNewDomain_shouldBeSuccessful() {
		String accountId = UUID.randomUUID().toString();
		String domain = RandomStringUtils.randomAlphanumeric(8) + ".com";
		ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);

		tested.addDomain(accountId, domain);

		verify(restTemplateMock).postForLocation(urlArgumentCaptor.capture(), any());
		assertThat(urlArgumentCaptor.getValue()).contains(accountId, domain);
	}

	@Test
	public void testAddDomain_givenExistingDomainError_shouldHandleException() {
		when(restTemplateMock.postForLocation(anyString(), any())).thenThrow(DomainAlreadyExistsException.class);

		tested.addDomain(UUID.randomUUID().toString(), RandomStringUtils.randomAscii(8) + ".com");
	}

	@Test
	public void testDeleteDomain_givenExistingDomain_shouldBeSuccessful() {
		String accountId = UUID.randomUUID().toString();
		String domain = RandomStringUtils.randomAlphanumeric(8) + ".com";
		ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);

		tested.deleteDomain(accountId, domain);

		verify(restTemplateMock).delete(urlArgumentCaptor.capture());
		assertThat(urlArgumentCaptor.getValue()).contains(accountId, domain);
	}

	@Test
	public void testDeleteDomain_givenNonExistingDomain_shouldHandleException() {
		doThrow(DomainNotFoundException.class).when(restTemplateMock).delete(anyString());

		tested.deleteDomain(UUID.randomUUID().toString(), RandomStringUtils.randomAscii(8) + ".com");
	}
}
