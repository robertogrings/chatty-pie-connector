package com.chattypie.datasource;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.SQLTemplatesRegistry;

@Configuration
public class DatasourceConfiguration {

	@Bean
	public SQLQueryFactory nonTransactionalQueryFactory(JdbcTemplate jdbcTemplate, DataSource dataSource) {
		ConnectionCallback<SQLTemplates> sqlTemplatesConnectionCallback = connection -> new SQLTemplatesRegistry().getTemplates(connection.getMetaData());
		SQLTemplates templates = jdbcTemplate.execute(sqlTemplatesConnectionCallback);
		return new SQLQueryFactory(new com.querydsl.sql.Configuration(templates), dataSource);
	}
}
