package com.chattypie.service.appmarket;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.Optional;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringRunner;

import com.chattypie.datasource.DatasourceConfiguration;
import com.chattypie.persistence.model.CompanyAccount;
import com.chattypie.util.OptionalLocalDatasourceConfiguration;
import com.chattypie.util.TestDatabaseHandle;

@RunWith(SpringRunner.class)
@ContextHierarchy({
	@ContextConfiguration(classes = {DatasourceConfiguration.class, OptionalLocalDatasourceConfiguration.class}),
	@ContextConfiguration(classes = {CompanyAccountDaoIntegrationTestConfig.class})
})
public class CompanyAccountDaoIntegrationTest {

	@Autowired
	CompanyAccountDao testedDao;

	@Autowired
	TestDatabaseHandle testDatabaseHandle;

	@After
	public void cleanDatabase() throws SQLException {
		testDatabaseHandle.truncateSchema("chatty_pie_connector");
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
