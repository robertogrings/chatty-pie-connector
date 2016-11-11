package com.chattypie.service.appmarket;

class DatabaseException extends RuntimeException {
	DatabaseException(String errorMessage, Exception e) {
		super(errorMessage, e);
	}
}
