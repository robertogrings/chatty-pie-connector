package com.chattypie;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class LocalizedMessageServiceImpl implements LocalizedMessageService {

	private final MessageSource messageSource;

	public LocalizedMessageServiceImpl(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public String get(String messageId) {
		return messageSource.getMessage(
				messageId,
				new Object[]{},
				//By default, Spring Boot reads the locale for a request from the Accept-Language of the incoming request.
				//If not present, the default en-US will be used.
				LocaleContextHolder.getLocale()
		);
	}
}
