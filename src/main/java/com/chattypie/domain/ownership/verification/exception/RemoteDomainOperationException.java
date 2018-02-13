package com.chattypie.domain.ownership.verification.exception;

class RemoteDomainOperationException extends RuntimeException {
	private static final long serialVersionUID = 7341685364741533469L;

	RemoteDomainOperationException(String message) {
		super(message);
	}

	RemoteDomainOperationException(String message, Throwable cause) {
		super(message, cause);
	}
}
