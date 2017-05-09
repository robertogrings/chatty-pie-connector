package com.chattypie;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class LocalizationConfiguration {

	@Bean("localizedMessageSource")
	public MessageSource reloadableResourceBundleMessageSource() {
		ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
		reloadableResourceBundleMessageSource.setBasename("classpath:locale/messages");
		reloadableResourceBundleMessageSource.setCacheSeconds(3600);
		return reloadableResourceBundleMessageSource;
	}
}
