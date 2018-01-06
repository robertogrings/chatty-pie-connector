package com.chattypie.domain.ownership.verification.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import com.fasterxml.jackson.core.JsonParseException;

@RunWith(MockitoJUnitRunner.class)
public class DomainResponseErrorHandlerTest {
	@Mock
	private ClientHttpResponse clientHttpResponse;
	private DomainResponseErrorHandler domainResponseErrorHandler;

	@Before
	public void before() {
		domainResponseErrorHandler = new DomainResponseErrorHandler();
	}

	@Test
	public void testHasError_given4xxError_shouldReturnTrue() throws Exception {
		when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);

		assertThat(domainResponseErrorHandler.hasError(clientHttpResponse)).isTrue();
	}

	@Test
	public void testHasError_given5xxError_shouldReturnTrue() throws Exception {
		when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.NOT_IMPLEMENTED);

		assertThat(domainResponseErrorHandler.hasError(clientHttpResponse)).isTrue();
	}

	@Test
	public void testHasError_givenNon4xxOr5xxError_shouldReturnFalse() throws Exception {
		when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.OK);

		assertThat(domainResponseErrorHandler.hasError(clientHttpResponse)).isFalse();
	}

	@Test
	public void testHandleError_givenUnReadableResponseBody_shouldThrowRemoteDomainOperationException() throws Exception {
		when(clientHttpResponse.getBody()).thenThrow(IOException.class);

		assertThatExceptionOfType(RemoteDomainOperationException.class)
			.isThrownBy(() -> domainResponseErrorHandler.handleError(clientHttpResponse))
			.is(new Condition<>(e -> e.getCause() instanceof IOException, "The cause is expected to be IOException"));
	}

	@Test
	public void testHandleError_givenUnParsableResponseBody_shouldThrowRemoteDomainOperationException() throws Exception {
		when(clientHttpResponse.getBody()).thenReturn(IOUtils.toInputStream(RandomStringUtils.randomAscii(8), StandardCharsets.UTF_8));

		assertThatExceptionOfType(RemoteDomainOperationException.class)
			.isThrownBy(() -> domainResponseErrorHandler.handleError(clientHttpResponse))
			.is(new Condition<>(e -> e.getCause() instanceof JsonParseException, "The cause is expected to be JsonParseException"));
	}

	@Test
	public void testHandleError_givenConflictErrorResponse_shouldThrowDomainAlreadyExistsException() throws Exception {
		String remoteErrorMessage = "Domain Already Exists";
		when(clientHttpResponse.getStatusCode()).thenReturn(CONFLICT);
		when(clientHttpResponse.getBody()).thenReturn(IOUtils.toInputStream(buildJsonErrorMessage(remoteErrorMessage), StandardCharsets.UTF_8));

		assertThatExceptionOfType(DomainAlreadyExistsException.class)
			.isThrownBy(() -> domainResponseErrorHandler.handleError(clientHttpResponse))
			.is(new Condition<>(e -> e.getMessage().equals(remoteErrorMessage), "Expected a conflict error with message " + remoteErrorMessage));
	}

	@Test
	public void testHandleError_givenNotFoundErrorResponse_shouldThrowDomainNotFoundException() throws Exception {
		String remoteErrorMessage = "Domain Not Found";
		when(clientHttpResponse.getStatusCode()).thenReturn(NOT_FOUND);
		when(clientHttpResponse.getBody()).thenReturn(IOUtils.toInputStream(buildJsonErrorMessage(remoteErrorMessage), StandardCharsets.UTF_8));

		assertThatExceptionOfType(DomainNotFoundException.class)
			.isThrownBy(() -> domainResponseErrorHandler.handleError(clientHttpResponse))
			.is(new Condition<>(e -> e.getMessage().equals(remoteErrorMessage), "Expected a not found error with message " + remoteErrorMessage));
	}

	@Test
	public void testHandleError_givenUnexpectedErrorResponse_shouldThrowRemoteDomainOperationException() throws Exception {
		String remoteErrorMessage = "Some error";
		when(clientHttpResponse.getStatusCode()).thenReturn(BAD_REQUEST);
		when(clientHttpResponse.getBody()).thenReturn(IOUtils.toInputStream(buildJsonErrorMessage(remoteErrorMessage), StandardCharsets.UTF_8));

		assertThatExceptionOfType(RemoteDomainOperationException.class)
			.isThrownBy(() -> domainResponseErrorHandler.handleError(clientHttpResponse))
			.is(new Condition<>(e -> e.getMessage().equals(remoteErrorMessage), "Unexpected error with message " + remoteErrorMessage));
	}

	private String buildJsonErrorMessage(String remoteErrorMessage) {
		return "{\"error\":\"" + remoteErrorMessage + "\"}";
	}
}
