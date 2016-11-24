package com.chattypie;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.appdirect.sdk.appmarket.AppmarketEventHandler;
import com.chattypie.util.OptionalLocalDatasourceConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RootConfiguration.class, OptionalLocalDatasourceConfiguration.class})
public class ChattyPieConnectorApplicationTests {

	@Autowired
	private
	ApplicationContext context;

	@Test
	public void testContextLoads_whenContextLoaded_theExpectedNumberOfHandlerBeansIsDefined() {
		//Given
		int expectedNumberOfDefinedEventHandlers = 7;

		//When
		Map<String, AppmarketEventHandler> beansOfType = context.getBeansOfType(AppmarketEventHandler.class);

		//Then
		assertThat(beansOfType.size()).isEqualTo(expectedNumberOfDefinedEventHandlers);
	}
}
