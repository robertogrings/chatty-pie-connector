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
import com.appdirect.sdk.appmarket.DeveloperSpecificAppmarketCredentialsSupplier;
import com.appdirect.sdk.credentials.StringBackedCredentialsSupplier;
import com.appdirect.sdk.notification.HtmlEmailNotificationService;
import com.chattypie.handler.EventHandlersConfiguration;
import com.chattypie.service.appmarket.CompanyAccountServiceConfiguration;
import com.chattypie.service.chattypie.ChattyPieAccessConfiguration;
import com.chattypie.service.chattypie.chatroom.ChatroomService;
import com.chattypie.service.chattypie.greeting.EmailContentGenerator;
import com.chattypie.service.chattypie.greeting.EmailNotificationService;
import com.chattypie.service.chattypie.greeting.NotificationService;
import com.chattypie.web.ReportGenerationController;

@Configuration
@Import({
	ConnectorSdkConfiguration.class,
	EventHandlersConfiguration.class,
	CompanyAccountServiceConfiguration.class,
	ChattyPieAccessConfiguration.class
})
@EnableAutoConfiguration
public class RootConfiguration {

	@Bean
	public DeveloperSpecificAppmarketCredentialsSupplier environmentCredentialsSupplier(@Value("${connector.allowed.credentials}") String allowedCredentials) {
		return new StringBackedCredentialsSupplier(allowedCredentials);
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
	@SuppressWarnings("SpringJavaAutowiringInspection")
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
	public EmailContentGenerator emailContentGenerator() {
		return new EmailContentGenerator();
	}

	@Bean
	public ReportGenerationController reportGenerationController(
		ChatroomService chatroomService,
		NotificationService notificationService,
		@Value("${chatroom.report.subscriber}") String chatroomReportSubscriberEmail) {

		return new ReportGenerationController(
			chatroomService,
			notificationService,
			chatroomReportSubscriberEmail
		);
	}
}
