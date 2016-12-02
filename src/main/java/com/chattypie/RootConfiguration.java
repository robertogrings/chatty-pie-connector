package com.chattypie;

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
import com.appdirect.sdk.appmarket.Credentials;
import com.appdirect.sdk.appmarket.DeveloperSpecificAppmarketCredentialsSupplier;
import com.appdirect.sdk.appmarket.events.SubscriptionCancel;
import com.appdirect.sdk.appmarket.events.SubscriptionChange;
import com.appdirect.sdk.appmarket.events.SubscriptionClosed;
import com.appdirect.sdk.appmarket.events.SubscriptionDeactivated;
import com.appdirect.sdk.appmarket.events.SubscriptionOrder;
import com.appdirect.sdk.appmarket.events.SubscriptionReactivated;
import com.appdirect.sdk.appmarket.events.SubscriptionUpcomingInvoice;
import com.appdirect.sdk.notification.HtmlEmailNotificationService;
import com.chattypie.handler.SubscriptionCancelHandler;
import com.chattypie.handler.SubscriptionChangeHandler;
import com.chattypie.handler.SubscriptionClosedHandler;
import com.chattypie.handler.SubscriptionDeactivatedHandler;
import com.chattypie.handler.SubscriptionOrderHandler;
import com.chattypie.handler.SubscriptionReactivatedHandler;
import com.chattypie.handler.SubscriptionUpcomingInvoiceHandler;
import com.chattypie.service.appmarket.CompanyAccountService;
import com.chattypie.service.appmarket.CompanyAccountServiceConfiguration;
import com.chattypie.service.chattypie.ChattyPieAccessConfiguration;
import com.chattypie.service.chattypie.chatroom.ChatroomService;
import com.chattypie.service.chattypie.greeting.EmailContentGenerator;
import com.chattypie.service.chattypie.greeting.EmailGreetingService;
import com.chattypie.service.chattypie.greeting.GreetingService;

@Configuration
@Import({
	ConnectorSdkConfiguration.class,
	CompanyAccountServiceConfiguration.class,
	ChattyPieAccessConfiguration.class
})
@EnableAutoConfiguration
public class RootConfiguration {

	@Bean
	public DeveloperSpecificAppmarketCredentialsSupplier credentialsSupplier() {
		return consumerKey -> new Credentials(consumerKey, "xBzbtLgp1V7m");
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
	public GreetingService newCompanyGreetingService(EmailContentGenerator contentGenerator,
													 HtmlEmailNotificationService emailNotificationService) {
		return new EmailGreetingService(
			contentGenerator,
			emailNotificationService,
			"Welcome to Chatty Pie!"
		);
	}

	@Bean
	public AppmarketEventHandler<SubscriptionOrder> subscriptionOrderHandler(
		CompanyAccountService companyAccountService,
		ChatroomService chatroomService,
		GreetingService greetingsService) {

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
	public ErrorHandler errorHandler() {
		return new ErrorHandler();
	}

	@Bean
	public HtmlEmailNotificationService notificationService(JavaMailSender javaMailSender) {
		String fromAddress = "notifications@appdirect.com";
		return new HtmlEmailNotificationService(fromAddress, javaMailSender);
	}

	@Bean
	public EmailContentGenerator emailContentGenerator() {
		return new EmailContentGenerator();
	}
}
