package com.chattypie.util;

import java.io.IOException;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chattypie.datasource.LocalDatasourceFactory;

/**
 * An alternative datasource configuration module meant for running locally, against a database ran in
 * a docker container.
 */
@Configuration
@EnableAutoConfiguration
//Enables Flyway to kick in automagically and migrate the database, same as when the real app starts
public class OptionalLocalDatasourceConfiguration {

	@Bean
	public LocalDatasourceFactory datasourceFactory() throws IOException {
		return new LocalDatasourceFactory();
	}

	@Bean
	public DataSource testDatasource(LocalDatasourceFactory datasourceFactory) {
		return datasourceFactory.getDatasource();
	}

	@PreDestroy
	public void removeLocalDbInstance() throws IOException {
		//NB! : Very important to have the removeLocalDbInstance method here: when we are finished using a container,
		//		we should remove it to avoid resource leaks
		datasourceFactory().shutdown();
	}
}
