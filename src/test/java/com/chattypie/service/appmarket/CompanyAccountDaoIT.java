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

package com.chattypie.service.appmarket;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.chattypie.datasource.DatasourceConfiguration;
import com.chattypie.persistence.model.CompanyAccount;
import com.chattypie.util.ITDatabaseUtils;
import com.chattypie.util.ITTestDataSourceConfiguration;
import com.chattypie.util.TestDatabaseHandle;
import com.querydsl.sql.SQLQueryFactory;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {DatasourceConfiguration.class, ITTestDataSourceConfiguration.class})
public class CompanyAccountDaoIT {
	@Autowired
	private SQLQueryFactory queryFactory;

	private TestDatabaseHandle testDatabaseHandle;

	private CompanyAccountDao testedDao;

	@Before
	public void setUp() throws Exception {
		testDatabaseHandle = new TestDatabaseHandle(ITDatabaseUtils.readTestDatabaseUrl());
		testedDao = new CompanyAccountDao(queryFactory);
	}

	@After
	public void cleanDatabase() throws SQLException {
		testDatabaseHandle.truncateSchema();
	}

	@Test
	public void test() {
		//Given
		String testCompanyId = "testCompanyId";
		String testAccountId = "testAccountId";
		CompanyAccount companyAccountsPersisted = new CompanyAccount(testAccountId, testCompanyId);

		//When
		testedDao.insert(companyAccountsPersisted);
		Optional<CompanyAccount> companyAccountsRead = testedDao.readById(testCompanyId);

		//Then
		assertThat(companyAccountsRead).isPresent();
		assertThat(companyAccountsRead.get().getId()).isEqualTo(testCompanyId);
		assertThat(companyAccountsRead.get().getAccountId()).isEqualTo(testAccountId);
	}
}
