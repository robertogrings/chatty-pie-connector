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
