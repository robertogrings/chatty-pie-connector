package com.chattypie.feature;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.chattypie.RootConfiguration;
import com.chattypie.support.fake.WithFakeChattyPie;
import com.chattypie.support.fake.WithFakeEmailServer;
import com.google.common.collect.Lists;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RootConfiguration.class, webEnvironment = RANDOM_PORT)
public class CanValidatePurchaseOrderIT {

	@LocalServerPort
	private int localConnectorPort;

	private RestTemplate restTemplate = new RestTemplate();

	@Test
	public void testOrderValidationEndpoint_whenAcceptedLanguageSetToEnglish_thenEnglishErrorsAreReturned() throws Exception {
		//Given
		MultiValueMap<String, String> expectedFields = new LinkedMultiValueMap<>();
		expectedFields.put("aDummyField", Lists.newArrayList("aDummyFieldValue"));
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Accept-Language", "en");

		//When
		ResponseEntity<String> stringResponseEntity = restTemplate.exchange(
				connectorValidationEndpoint(),
				HttpMethod.POST,
				new HttpEntity<>(expectedFields, headers),
				String.class
		);

		//Then
		assertThat(stringResponseEntity.getBody()).containsIgnoringCase("You must include a contact name");
	}

	@Test
	public void testOrderValidationEndpoint_whenAcceptedLanguageSetToFrench_thenFrenchErrorsAreReturned() throws Exception {
		//Given
		MultiValueMap<String, String> expectedFields = new LinkedMultiValueMap<>();
		expectedFields.put("aDummyField", Lists.newArrayList("aDummyFieldValue"));
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Accept-Language", "fr");

		//When
		ResponseEntity<String> stringResponseEntity = restTemplate.exchange(
				connectorValidationEndpoint(),
				HttpMethod.POST,
				new HttpEntity<>(expectedFields, headers),
				String.class
		);

		//Then
		assertThat(stringResponseEntity.getBody()).containsIgnoringCase("Nom du contact");
	}

	private String connectorValidationEndpoint() {
		return String.format("http://localhost:%s/unsecured/integration/orderValidation", localConnectorPort);
	}
}
