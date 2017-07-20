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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import com.chattypie.support.ContentOf;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(MockitoJUnitRunner.class)
public class DomainOperationsServiceITTest {

	@Rule
	public WireMockRule mockServer = new WireMockRule(options().dynamicPort());

	private DomainOperationsService tested;

	private String chattyPieHost;

	private ObjectMapper jsonMapper = new ObjectMapper();

	@Before
	public void setUp() throws Exception {
		chattyPieHost = format("http://localhost:%d", mockServer.port());
		tested = new DomainOperationsService(new RestTemplate(), chattyPieHost);
	}

	@Test
	public void testGetDomainOwnershipProof_whenCalled_theResponseShouldBeProperlySerialized() throws Exception {
		//Given
		String testDomain = "dummy.com";
		String testAccountId = UUID.randomUUID().toString();

		final String ownershipRecordUrl = format("/accounts/%s/domains/%s/ownershipProof", testAccountId, testDomain);
		final String ownershipProofPayload = ContentOf.resourceAsString("chatty-pie-responses/ownership-proof.json");
		final ChattyPieDomainOwnershipProof expectedOwnershipProof = jsonMapper.readValue(
				ownershipProofPayload,
				ChattyPieDomainOwnershipProof.class
		);

		mockServer.givenThat(
				get(
						urlEqualTo(ownershipRecordUrl)
				).willReturn(
						aResponse()
								.withStatus(200)
								.withHeader("Content-Type", "application/json")
								.withBody(ownershipProofPayload)
				)
		);

		//When
		final ChattyPieDomainOwnershipProof actualDomainOwnershipProof = tested.getDomainOwnershipProof(
				testAccountId,
				testDomain
		);

		//Then
		assertThat(actualDomainOwnershipProof).isEqualTo(expectedOwnershipProof);
	}

	@Test
	public void testTriggerDomainOwnershipVerification_whenCalled_aPostIsIssuedToTheAppropriateEndpoint() throws Exception {
		//Given
		String testDomain = "dummy.com";
		String testAccountId = UUID.randomUUID().toString();
		String triggerDomainVerificationUrl = format(
				"/accounts/%s/domains/%s/triggerOwnershipVerification",
				testAccountId,
				testDomain
		);

		mockServer.givenThat(
				post(
						urlEqualTo(triggerDomainVerificationUrl)
				).willReturn(
						aResponse().withStatus(200)
				)
		);

		//When
		tested.triggerDomainOwnerhshipVerification(testAccountId, testDomain);

		//Then
		mockServer.verify(
				postRequestedFor(
						urlEqualTo(triggerDomainVerificationUrl)
				)
		);
	}

	@Test
	public void testTriggerDomainOwnershipVerification_whenApiCallReturnsError_theMethodThrows() throws Exception {
		//Given
		String testDomain = "dummy.com";
		String testAccountId = UUID.randomUUID().toString();
		String triggerDomainVerificationUrl = format(
				"/accounts/%s/domains/%s/triggerOwnershipVerification",
				testAccountId,
				testDomain
		);

		mockServer.givenThat(
				post(
						urlEqualTo(triggerDomainVerificationUrl)
				).willReturn(
						aResponse().withStatus(400)
				)
		);

		//When
		final Throwable throwable = catchThrowable(() ->
				tested.triggerDomainOwnerhshipVerification(testAccountId, testDomain)
		);

		//Then
		assertThat(throwable).isNotNull();
	}
}
