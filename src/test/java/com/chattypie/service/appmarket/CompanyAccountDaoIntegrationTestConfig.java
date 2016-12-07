package com.chattypie.service.appmarket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.chattypie.util.TestDatabaseHandle;
import com.querydsl.sql.SQLQueryFactory;

@Configuration
public class CompanyAccountDaoIntegrationTestConfig {

	@Bean
	public TestDatabaseHandle testDatabaseUtils(JdbcTemplate jdbcTemplate, SQLQueryFactory testFactory) throws Exception {
		return new TestDatabaseHandle(jdbcTemplate, testFactory);
	}

	@Bean
	public CompanyAccountDao testedDao(SQLQueryFactory testFactory) {
		return new CompanyAccountDao(testFactory);
	}
}
