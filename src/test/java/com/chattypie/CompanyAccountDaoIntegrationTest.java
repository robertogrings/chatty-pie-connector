package com.chattypie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.chattypie.persistence.model.CompanyAccount;
import com.chattypie.service.appmarket.CompanyAccountDao;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ChattyPieConnectorApplication.class}, webEnvironment = NONE)
@ActiveProfiles("IT")
public class CompanyAccountDaoIntegrationTest {

	@Autowired
	public CompanyAccountDao companyAccountDao;

	@Test
	@Transactional
	public void testInsertAndRead() throws Exception {
		//Given
		String testCompanyId = "testCompanyId";
		String testAccountId = "testAccountId";
		CompanyAccount companyAccountsPersisted = new CompanyAccount(testAccountId, testCompanyId);

		//When
		companyAccountDao.insert(companyAccountsPersisted);
		Optional<CompanyAccount> companyAccountsRead = companyAccountDao.readById(testCompanyId);

		//Then
		assertThat(companyAccountsRead).isPresent();
		assertThat(companyAccountsRead.get().getId()).isEqualTo(testCompanyId);
		assertThat(companyAccountsRead.get().getAccountId()).isEqualTo(testAccountId);
	}
}
