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

package com.chattypie.datasource;

import java.io.IOException;

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
	public JdbcTemplate jdbcTemplate(DataSource dataSource) throws IOException, InterruptedException {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	public SQLQueryFactory nonTransactionalQueryFactory(JdbcTemplate jdbcTemplate, DataSource dataSource) {
		ConnectionCallback<SQLTemplates> sqlTemplatesConnectionCallback = connection -> new SQLTemplatesRegistry().getTemplates(connection.getMetaData());
		SQLTemplates templates = jdbcTemplate.execute(sqlTemplatesConnectionCallback);
		return new SQLQueryFactory(new com.querydsl.sql.Configuration(templates), dataSource);
	}
}
