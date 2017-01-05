package com.chattypie;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;

import ch.qos.logback.access.tomcat.LogbackValve;
import com.appdirect.sdk.ConnectorSdkConfiguration;
import com.appdirect.sdk.appmarket.AppmarketEventHandler;
import com.appdirect.sdk.appmarket.DeveloperSpecificAppmarketCredentialsSupplier;
import com.appdirect.sdk.appmarket.events.AddonSubscriptionOrder;
import com.appdirect.sdk.appmarket.events.SubscriptionCancel;
import com.appdirect.sdk.appmarket.events.SubscriptionChange;
import com.appdirect.sdk.appmarket.events.SubscriptionClosed;
import com.appdirect.sdk.appmarket.events.SubscriptionDeactivated;
import com.appdirect.sdk.appmarket.events.SubscriptionOrder;
import com.appdirect.sdk.appmarket.events.SubscriptionReactivated;
import com.appdirect.sdk.appmarket.events.SubscriptionUpcomingInvoice;
import com.appdirect.sdk.appmarket.events.UserAssignment;
import com.appdirect.sdk.appmarket.events.UserUnassignment;
import com.appdirect.sdk.notification.HtmlEmailNotificationService;
import com.chattypie.handler.SubscriptionCancelHandler;
import com.chattypie.handler.SubscriptionChangeHandler;
import com.chattypie.handler.SubscriptionClosedHandler;
import com.chattypie.handler.SubscriptionDeactivatedHandler;
import com.chattypie.handler.SubscriptionOrderHandler;
import com.chattypie.handler.SubscriptionReactivatedHandler;
import com.chattypie.handler.SubscriptionUpcomingInvoiceHandler;
import com.chattypie.handler.UserAssignmentHandler;
import com.chattypie.handler.UserUnassignmentHandler;
import com.chattypie.service.appmarket.CompanyAccountService;
import com.chattypie.service.appmarket.CompanyAccountServiceConfiguration;
import com.chattypie.service.chattypie.ChattyPieAccessConfiguration;
import com.chattypie.service.chattypie.chatroom.ChatroomService;
import com.chattypie.service.chattypie.greeting.EmailContentGenerator;
import com.chattypie.service.chattypie.greeting.EmailNotificationService;
import com.chattypie.service.chattypie.greeting.NotificationService;
import com.chattypie.util.MapBuilder;
import com.chattypie.web.ReportGenerationController;
import com.chattypie.web.StringBackedCredentialsSupplier;

@Configuration
@Import({
	ConnectorSdkConfiguration.class,
	CompanyAccountServiceConfiguration.class,
	ChattyPieAccessConfiguration.class
})
@EnableAutoConfiguration
public class RootConfiguration {

	@Value("${chatroom.report.subscriber}")
	String chatroomReportSubscriberEmail;

	@Bean
	public DeveloperSpecificAppmarketCredentialsSupplier environmentCredentialsSupplier(@Value("${connector.allowed.credentials}") String allowedCredentials) {
		return new StringBackedCredentialsSupplier(allowedCredentials, new MapBuilder());
	}

	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
		// taken from http://stackoverflow.com/a/36790613/26605
		TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
		LogbackValve logbackValve = new LogbackValve();
		logbackValve.setQuiet(true);
		logbackValve.setFilename("logback-access.xml");
		tomcat.addContextValves(logbackValve);
		return tomcat;
	}

	@Bean
	public HtmlEmailNotificationService emailNotificationService(JavaMailSender javaMailSender) {
		return new HtmlEmailNotificationService("do-not-reply@appdirect.com", javaMailSender);
	}

	@Bean
	public ExecutorService executorService() {
		return Executors.newFixedThreadPool(1);
	}

	@Bean
	public NotificationService newCompanyGreetingService(EmailContentGenerator contentGenerator,
														 HtmlEmailNotificationService emailNotificationService) {
		return new EmailNotificationService(
			contentGenerator,
			emailNotificationService
		);
	}

	@Bean
	public AppmarketEventHandler<AddonSubscriptionOrder> addonSubscriptionOrderAppmarketEventHandler() {
		return null;
	}

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

	@Bean
	public AppmarketEventHandler<SubscriptionChange> subscriptionChangeHandler() {
		return new SubscriptionChangeHandler();
	}

	@Bean
	public AppmarketEventHandler<SubscriptionDeactivated> subscriptionDeactivatedAppmarketEventHandler(ChatroomService chatroomService) {
		return new SubscriptionDeactivatedHandler(chatroomService);
	}

	@Bean
	public AppmarketEventHandler<SubscriptionReactivated> subscriptionReactivatedAppmarketEventHandler(ChatroomService chatroomService) {
		return new SubscriptionReactivatedHandler(chatroomService);
	}

	@Bean
	public AppmarketEventHandler<SubscriptionClosed> subscriptionClosedAppmarketEventHandler(ChatroomService chatroomService) {
		return new SubscriptionClosedHandler(chatroomService);
	}

	@Bean
	public AppmarketEventHandler<SubscriptionUpcomingInvoice> subscriptionUpcomingInvoiceAppmarketEventHandler() {
		return new SubscriptionUpcomingInvoiceHandler();
	}

	@Bean
	public AppmarketEventHandler<UserAssignment> userAssignmentAppmarketEventHandler() {
		return new UserAssignmentHandler();
	}

	@Bean
	public AppmarketEventHandler<UserUnassignment> userUnassignmentAppmarketEventHandler() {
		return new UserUnassignmentHandler();
	}

	@Bean
	public ErrorHandler errorHandler() {
		return new ErrorHandler();
	}

	@Bean
	public EmailContentGenerator emailContentGenerator() {
		return new EmailContentGenerator();
	}

	@Bean
	public ReportGenerationController reportGenerationController(
		ChatroomService chatroomService,
		NotificationService notificationService) {

		return new ReportGenerationController(
			chatroomService,
			notificationService,
			chatroomReportSubscriberEmail
		);
	}
}
