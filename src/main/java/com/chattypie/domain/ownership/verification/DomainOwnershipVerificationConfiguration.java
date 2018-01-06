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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

import com.appdirect.sdk.appmarket.domain.DomainAdditionHandler;
import com.appdirect.sdk.appmarket.domain.DomainDnsOwnershipVerificationConfiguration;
import com.appdirect.sdk.appmarket.domain.DomainDnsVerificationInfoHandler;
import com.appdirect.sdk.appmarket.domain.DomainOwnershipVerificationHandler;
import com.appdirect.sdk.appmarket.domain.DomainRemovalHandler;
import com.appdirect.sdk.appmarket.domain.DomainVerificationNotificationClient;
import com.chattypie.domain.ownership.verification.exception.DomainResponseErrorHandler;
import com.chattypie.service.chattypie.chatroom.ChatroomService;

public class DomainOwnershipVerificationConfiguration extends DomainDnsOwnershipVerificationConfiguration {

	@Value("${chatty.pie.host}")
	public String chattyPieHost;

	@Autowired
	private RestTemplate chattyPieRestTemplate;

	@Autowired
	private ChatroomService chatroomService;

	@Autowired
	private DomainVerificationNotificationClient domainVerificationNotificationClient;

	@Bean
	public DomainOperationsService domainOperationsService() {
		chattyPieRestTemplate.setErrorHandler(new DomainResponseErrorHandler());

		return new DomainOperationsService(
				chattyPieRestTemplate,
				chattyPieHost
		);
	}

	@Bean
	public RetryTemplate retryTemplate() {
		final RetryTemplate retryTemplate = new RetryTemplate();
		FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
		fixedBackOffPolicy.setBackOffPeriod(2000l);
		retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

		SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
		retryPolicy.setMaxAttempts(3);
		retryTemplate.setRetryPolicy(retryPolicy);

		return retryTemplate;
	}

	@Bean
	public ChattyPieDomainOwnershipVerificationHandlerWithCallback innerHandler() {
		return new ChattyPieDomainOwnershipVerificationHandlerWithCallback(chatroomService,
				domainOperationsService(),
				domainVerificationNotificationClient,
				retryTemplate()
		);
	}

	@Override
	public DomainDnsVerificationInfoHandler domainDnsVerificationInfoHandler() {
		return new ChattyPieDomainVerificationInfoHandler(chatroomService, domainOperationsService());
	}

	@Override
	public DomainOwnershipVerificationHandler domainOwnershipVerificationHandler(DomainVerificationNotificationClient client) {
		return new ChattyPieDomainOwnershipVerificationHandler(innerHandler());
	}

	@Override
	public DomainAdditionHandler domainAdditionHandler() {
		return new ChattyPieDomainAdditionHandler(domainOperationsService());
	}

	@Override
	public DomainRemovalHandler domainRemovalHandler() {
		return new ChattyPieDomainRemovalHandler(domainOperationsService());
	}
}
