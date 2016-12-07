package com.chattypie.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;

public class ResourceUtilsTest {

	@Test
	public void readResourceFileAsString_wherResourceFileIsPresent_thenItsContentsAreReturnedAsString() throws Exception {
		//Given
		String testResourcePath = "SampleResource.txt";

		//When
		String resourceFileContents = ResourceUtils.readResourceFileAsString(testResourcePath);

		//Then
		assertThat(resourceFileContents).isEqualTo("Hello World !\n");
	}

	@Test
	public void readResourceAsString_whenResourceNotFound_aRuntimeExceptionOccurs() throws Exception {
		//Given
		String testResourcePath = "NonExistingResource.txt";

		//Then
		assertThatThrownBy(() -> ResourceUtils.readResourceFileAsString(testResourcePath))
			.isInstanceOf(RuntimeException.class);
	}
}
