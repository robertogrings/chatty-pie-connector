package com.chattypie.domain.ownership.verification.exception;

public class DomainAlreadyExistsException extends RemoteDomainOperationException {
	private static final long serialVersionUID = -8842416126303022655L;

	DomainAlreadyExistsException(String message) {
		super(message);
	}
}
