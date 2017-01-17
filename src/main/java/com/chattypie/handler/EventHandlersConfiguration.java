package com.chattypie.handler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.appdirect.sdk.appmarket.AppmarketEventHandler;
import com.appdirect.sdk.appmarket.events.AddonSubscriptionCancel;
import com.appdirect.sdk.appmarket.events.SubscriptionCancel;
import com.appdirect.sdk.appmarket.events.SubscriptionChange;
import com.appdirect.sdk.appmarket.events.SubscriptionClosed;
import com.appdirect.sdk.appmarket.events.SubscriptionDeactivated;
import com.appdirect.sdk.appmarket.events.SubscriptionOrder;
import com.appdirect.sdk.appmarket.events.SubscriptionReactivated;
import com.appdirect.sdk.appmarket.events.SubscriptionUpcomingInvoice;
import com.appdirect.sdk.appmarket.events.UserAssignment;
import com.appdirect.sdk.appmarket.events.UserUnassignment;
import com.chattypie.service.appmarket.CompanyAccountService;
import com.chattypie.service.chattypie.chatroom.ChatroomService;
import com.chattypie.service.chattypie.greeting.NotificationService;

@Configuration
public class EventHandlersConfiguration {
	@Bean
	public AppmarketEventHandler<SubscriptionOrder> subscriptionOrderHandler(
			CompanyAccountService companyAccountService,
			ChatroomService chatroomService,
			NotificationService greetingsService) {

		return new SubscriptionOrderHandler(companyAccountService, chatroomService, greetingsService);
	}

	@Bean
	public AppmarketEventHandler<SubscriptionCancel> subscriptionCancelHandler(ChatroomService chatroomService) {
		return new SubscriptionCancelHandler(chatroomService);
	}

	@Primary
	@Bean
	public AppmarketEventHandler<SubscriptionChange> subscriptionChangeHandler() {
		return new SubscriptionChangeHandler();
	}

	@Primary
	@Bean
	public AppmarketEventHandler<SubscriptionDeactivated> subscriptionDeactivatedAppmarketEventHandler(ChatroomService chatroomService) {
		return new SubscriptionDeactivatedHandler(chatroomService);
	}

	@Primary
	@Bean
	public AppmarketEventHandler<SubscriptionReactivated> subscriptionReactivatedAppmarketEventHandler(ChatroomService chatroomService) {
		return new SubscriptionReactivatedHandler(chatroomService);
	}

	@Primary
	@Bean
	public AppmarketEventHandler<SubscriptionClosed> subscriptionClosedAppmarketEventHandler(ChatroomService chatroomService) {
		return new SubscriptionClosedHandler(chatroomService);
	}

	@Primary
	@Bean
	public AppmarketEventHandler<SubscriptionUpcomingInvoice> subscriptionUpcomingInvoiceAppmarketEventHandler() {
		return new SubscriptionUpcomingInvoiceHandler();
	}

	@Primary
	@Bean
	public AppmarketEventHandler<UserAssignment> userAssignmentAppmarketEventHandler() {
		return new UserAssignmentHandler();
	}

	@Primary
	@Bean
	public AppmarketEventHandler<UserUnassignment> userUnassignmentAppmarketEventHandler() {
		return new UserUnassignmentHandler();
	}

	@Primary
	@Bean
	public AddonSubscriptionOrderHandler addonOrderHandler(ChatroomService chatroomService) {
		return new AddonSubscriptionOrderHandler(chatroomService);
	}

	@Primary
	@Bean
	public AppmarketEventHandler<AddonSubscriptionCancel> addonSubscriptionCancelAppmarketEventHandler(ChatroomService chatroomService) {
		return new AddonSubscriptionCancelHandler(chatroomService);
	}
}
