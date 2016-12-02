package com.chattypie.service.chattypie.greeting;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class EmailContentGeneratorTest {

	private EmailContentGenerator testedContentGenerator = new EmailContentGenerator();

	@Test
	public void generateEmailContent_returnsAGreetingWithProperCompanyName() throws Exception {
		//Given
		String testCompanyName = "myCompanyName";
		String expectedEmailContent = "<HTML>\n" +
			"<HEAD>\n" +
			"  <TITLE>\n" +
			"    A Small Hello\n" +
			"  </TITLE>\n" +
			"</HEAD>\n" +
			"<BODY>\n" +
			"<H1>Hello myCompanyName </H1>\n" +
			"<P>This is very minimal \"hello world\" HTML document.</P>\n" +
			"</BODY>\n" +
			"</HTML>\n";

		//When
		String generatedEmailContent = testedContentGenerator.generateWelcomeEmail(testCompanyName);

		//Then
		assertThat(generatedEmailContent).isEqualTo(expectedEmailContent);
	}

}
