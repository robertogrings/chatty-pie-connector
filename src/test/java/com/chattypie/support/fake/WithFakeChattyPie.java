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
