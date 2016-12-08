package com.chattypie.service.appmarket;

class DatabaseException extends RuntimeException {
	private static final long serialVersionUID = 434678521;

	DatabaseException(String errorMessage, Exception e) {
		super(errorMessage, e);
	}
}
