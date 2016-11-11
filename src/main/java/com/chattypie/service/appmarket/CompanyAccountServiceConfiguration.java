package com.chattypie.service.appmarket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.chattypie.datasource.DatasourceConfiguration;
import com.chattypie.service.chattypie.ChattyPieAccessConfiguration;
import com.chattypie.service.chattypie.account.ChattyPieAccountService;
import com.querydsl.sql.SQLQueryFactory;

@Configuration
@Import({
	DatasourceConfiguration.class,
	ChattyPieAccessConfiguration.class
})
public class CompanyAccountServiceConfiguration {

	@Bean
	public CompanyAccountDao accountDao(SQLQueryFactory transactionalQueryFactory) {
		return new CompanyAccountDao(transactionalQueryFactory);
	}

	@Bean
	public CompanyAccountService accountService(CompanyAccountDao companyAccountDao, ChattyPieAccountService chattyPieAccountService) {
		return new CompanyAccountService(companyAccountDao, chattyPieAccountService);
	}
}
