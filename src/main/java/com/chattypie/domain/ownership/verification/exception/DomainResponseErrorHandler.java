package com.chattypie.domain.ownership.verification.exception;

import static java.util.Optional.ofNullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
public class DomainResponseErrorHandler implements ResponseErrorHandler {

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		HttpStatus status = response.getStatusCode();
		return status.is4xxClientError() || status.is5xxServerError();
	}

	@Override
	public void handleError(ClientHttpResponse response) {
		parseAndThrowException(response);
	}

	private void parseAndThrowException(ClientHttpResponse response) {
		Optional<DomainErrorResponse> domainErrorResponse;
		HttpStatus statusCode;

		try {
			domainErrorResponse = ofNullable(new ObjectMapper().readValue(IOUtils.toString(response.getBody(), StandardCharsets.UTF_8), DomainErrorResponse.class));
			statusCode = response.getStatusCode();
		} catch (IOException e) {
			log.error("Error parsing the error response", e);
			throw new RemoteDomainOperationException("Failed while parsing remote domain operation error", e);
		}

		domainErrorResponse.ifPresent(errorResponse -> {
			if (HttpStatus.CONFLICT == statusCode) {
				throw new DomainAlreadyExistsException(errorResponse.getError());
			} else if (HttpStatus.NOT_FOUND == statusCode) {
				throw new DomainNotFoundException(errorResponse.getError());
			} else {
				throw new RemoteDomainOperationException(errorResponse.getError());
			}
		});
	}

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class DomainErrorResponse {
		private String error;
	}
}
