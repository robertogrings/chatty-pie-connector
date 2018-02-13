package com.chattypie.domain.ownership.verification.exception;

public class DomainNotFoundException extends RemoteDomainOperationException {
	private static final long serialVersionUID = 3847977032003356386L;

	DomainNotFoundException(String message) {
		super(message);
	}
}
