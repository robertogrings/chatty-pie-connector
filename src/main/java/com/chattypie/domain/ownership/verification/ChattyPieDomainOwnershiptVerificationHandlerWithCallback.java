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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.support.RetryTemplate;

import com.appdirect.sdk.appmarket.domain.DomainVerificationNotificationClient;
import com.chattypie.service.chattypie.chatroom.ChatroomService;

@Slf4j
@RequiredArgsConstructor
public class ChattyPieDomainOwnershiptVerificationHandlerWithCallback {

	private final ChatroomService chatroomService;
	private final DomainOperationsService domainOperationsService;
	private final DomainVerificationNotificationClient domainVerificationNotificationClient;
	private final RetryTemplate retryTemplate;

	/**
	 * @param chatroomId  the chatroom whose owner account is to be verified to own the domain
	 * @param domain      the domain whose ownership we want to verify
	 * @param callbackUrl url at which we can reach the AppDirect Marketplace in order to report success / failure of the
	 *                    domain verification process.
	 * @param key         user key to authenticate with the AppDirect Marketplace upon callback
	 * @return a future that will be completed when the verification process completes (successfully or not)
	 */
	public Future<Void> verifyWithRetries(String chatroomId,
										  String domain,
										  String callbackUrl,
										  String key) {

		//The connector needs to retry with the triggering of verification on chatty pie, because at the time
		//the domain verification operation is called on the connector, the domain ownership records that the
		//AppDirect marketplace has set on the verified domain are not guaranteed to have been propagated, and
		//therefore might not be visible yet.

		//NB! This implementation of the retry is rather naive, in order to keep the example simple. In a real world 
		//    implementation, it is not advisable to block a thread while attempting domain verification, since this
		//    process could take a long time.
		return CompletableFuture.runAsync(() -> retryTemplate.execute(
				context -> {
					ChattyPieDomainOwnershiptVerificationHandlerWithCallback.this.triggerValidationOnChattyPieAndCallBackMarketplace(chatroomId, domain, callbackUrl, key);
					return null;
				},
				(RecoveryCallback<Void>) context -> {
					ChattyPieDomainOwnershiptVerificationHandlerWithCallback.this.signalFailureToTheMarketplace(chatroomId, domain, callbackUrl, key);
					return null;
				}
		));
	}

	private void triggerValidationOnChattyPieAndCallBackMarketplace(String chatroomId, String domain, String callbackUrl, String key) {
		domainOperationsService.triggerDomainOwnerhshipVerification(
				chatroomService.readIdOfAccountAssociatedWith(chatroomId),
				domain
		);
		domainVerificationNotificationClient.resolveDomainVerification(callbackUrl, key, true);
	}

	private void signalFailureToTheMarketplace(String chatroomId, String domain, String callbackUrl, String key) {
		try {
			domainVerificationNotificationClient.resolveDomainVerification(callbackUrl, key, false);
		} catch (Exception e) {
			final String errorMessage = format(
					"Failed reporting error to the AppDirect Marketplace on ownership verification " +
							"for chat room %s and domain %s",
					chatroomId,
					domain
			);
			log.error(errorMessage, e);
		}
	}
}
