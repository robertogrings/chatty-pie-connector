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

import lombok.RequiredArgsConstructor;

import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class DomainOperationsService {
	private static final String DOMAIN_OWNERSHIP_PROOF_URL_TEMPLATE = "%s/accounts/%s/domains/%s/ownershipProof";
	private static final String TRIGGER_OWNERSHIP_PROOF_URL_TEMPLATE = "%s/accounts/%s/domains/%s/triggerOwnershipVerification";
	private final RestTemplate restTemplate;
	private final String chattyPieHost;

	public ChattyPieDomainOwnershipProof getDomainOwnershipProof(String accountId, String domain) {
		return restTemplate.getForObject(
				format(DOMAIN_OWNERSHIP_PROOF_URL_TEMPLATE, chattyPieHost, accountId, domain),
				ChattyPieDomainOwnershipProof.class
		);
	}

	public void triggerDomainOwnerhshipVerification(String accountId, String domain) {
		restTemplate
				.postForLocation(
						format(TRIGGER_OWNERSHIP_PROOF_URL_TEMPLATE, chattyPieHost, accountId, domain),
						null,
						String.class
				);
	}
}
