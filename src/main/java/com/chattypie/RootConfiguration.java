package com.chattypie;

import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import ch.qos.logback.access.tomcat.LogbackValve;
import com.appdirect.sdk.ConnectorSdkConfiguration;
import com.appdirect.sdk.appmarket.AppmarketEventHandler;
import com.appdirect.sdk.appmarket.Credentials;
import com.appdirect.sdk.appmarket.DeveloperSpecificAppmarketCredentialsSupplier;
import com.appdirect.sdk.appmarket.api.SubscriptionCancel;
import com.appdirect.sdk.appmarket.api.SubscriptionChange;
import com.appdirect.sdk.appmarket.api.SubscriptionOrder;
import com.chattypie.handler.SubscriptionCancelHandler;
import com.chattypie.handler.SubscriptionChangeHandler;
import com.chattypie.handler.SubscriptionOrderHandler;
import com.chattypie.service.appmarket.CompanyAccountService;
import com.chattypie.service.appmarket.CompanyAccountServiceConfiguration;
import com.chattypie.service.chattypie.ChattyPieAccessConfiguration;
import com.chattypie.service.chattypie.chatroom.ChatroomService;

@Configuration
@Import({
		ConnectorSdkConfiguration.class,
		CompanyAccountServiceConfiguration.class,
		ChattyPieAccessConfiguration.class
})
public class RootConfiguration {

	@Bean
	public DeveloperSpecificAppmarketCredentialsSupplier credentialsSupplier() {
		return (consumerKey) -> new Credentials(consumerKey, "xBzbtLgp1V7m");
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
	public AppmarketEventHandler<SubscriptionOrder> subscriptionOrderHandler(CompanyAccountService companyAccountService, ChatroomService chatroomService) {
		return new SubscriptionOrderHandler(companyAccountService, chatroomService);
	}

	@Bean
	public AppmarketEventHandler<SubscriptionCancel> subscriptionCancelHandler(ChatroomService chatroomService) {
		return new SubscriptionCancelHandler(chatroomService);
	}

	@Bean
	public AppmarketEventHandler<SubscriptionChange> subscriptionChangeHandler() {
		return new SubscriptionChangeHandler();
	}
}
