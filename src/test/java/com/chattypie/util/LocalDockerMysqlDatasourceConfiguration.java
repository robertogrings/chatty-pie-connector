package com.chattypie.util;

import java.io.IOException;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * An alternative datasource configuration module meant for running locally, against a database ran in
 * a docker container.
 */
// Enables Flyway to kick in automagically and migrate the database, same as when the real app starts
@Configuration
@EnableAutoConfiguration
public class LocalDockerMysqlDatasourceConfiguration {
	@Bean
	public LocalDockerMysqlDatasourceFactory datasourceFactory() throws IOException {
		return new LocalDockerMysqlDatasourceFactory();
	}

	@Bean
	public MysqlDataSource testDatasource(LocalDockerMysqlDatasourceFactory localDockerMysqlDatasourceFactory) throws IOException {
		return localDockerMysqlDatasourceFactory.getDatasource();
	}
}
