package com.chattypie.support.fake;

import static org.springframework.test.context.support.TestPropertySourceUtils.addInlinedPropertiesToEnvironment;
import static org.springframework.util.SocketUtils.findAvailableTcpPort;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class WithFakeChattyPie implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		FakeChattyPie fakeChattyPie = startFakeChattyPie();

		ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
		beanFactory.registerSingleton(FakeChattyPie.class.getCanonicalName(), fakeChattyPie);

		addInlinedPropertiesToEnvironment(applicationContext, "chatty.pie.host=http://127.0.0.1:" + fakeChattyPie.getPort());
	}

	private FakeChattyPie startFakeChattyPie() {
		try {
			return FakeChattyPie.create(findAvailableTcpPort()).start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
