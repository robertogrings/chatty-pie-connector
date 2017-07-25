/*
 * Copyright 2017 AppDirect, Inc. and/or its affiliates
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chattypie.util;

import static com.chattypie.util.ITDatabaseUtils.readTestDatabaseUrl;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * An alternative datasource configuration module meant for providing a {@link javax.sql.DataSource}
 * pointing to a local IT database.
 */
@Configuration
@EnableAutoConfiguration // Enables Flyway to kick in automagically and migrate the database, same as when the real app starts
@ConfigurationProperties
@Slf4j
public class ITTestDataSourceConfiguration {

	@Bean
	public MysqlDataSource testDatasource() throws IOException {

		MysqlDataSource mysqlDataSource = new MysqlDataSource();
		String databaseUrl = readTestDatabaseUrl();
		log.info("Connecting to test DB at {}", databaseUrl);
		mysqlDataSource.setURL(databaseUrl);
		mysqlDataSource.setUser("root");
		mysqlDataSource.setPassword("");
		return mysqlDataSource;
	}

	@Bean
	public TestDatabaseHandle testDatabaseHandle(MysqlDataSource itTestDatasource) {
		return new TestDatabaseHandle(itTestDatasource.getUrl());
	}
}
