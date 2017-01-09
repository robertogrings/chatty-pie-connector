package com.chattypie.support.fake;

import static org.springframework.test.context.support.TestPropertySourceUtils.addInlinedPropertiesToEnvironment;
import static org.springframework.util.SocketUtils.findAvailableTcpPort;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class WithFakeEmailServer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		FakeEmailServer fakeEmailServer = FakeEmailServer.create(findAvailableTcpPort()).start();

		ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
		beanFactory.registerSingleton(FakeEmailServer.class.getCanonicalName(), fakeEmailServer);

		addInlinedPropertiesToEnvironment(applicationContext, "spring.mail.port=" + fakeEmailServer.getPort());
	}
}
