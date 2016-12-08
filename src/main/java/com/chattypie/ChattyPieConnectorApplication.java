package com.chattypie;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class ChattyPieConnectorApplication {

	private ConfigurableApplicationContext applicationContext;

	public static void main(String... args) {
		new ChattyPieConnectorApplication().start();
	}

	public ChattyPieConnectorApplication start(String... args) {
		applicationContext = SpringApplication.run(RootConfiguration.class, args);
		return this;
	}

	public int stop() {
		return SpringApplication.exit(applicationContext, () -> 0);
	}
}
