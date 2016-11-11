package com.chattypie.service.appmarket;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.chattypie.persistence.model.CompanyAccount;
import com.chattypie.service.chattypie.account.ChattyPieAccount;
import com.chattypie.service.chattypie.account.ChattyPieAccountService;

@RunWith(MockitoJUnitRunner.class)
public class CompanyAccountServiceTest {

	private CompanyAccountService testedAccountService;

	@Mock
	private CompanyAccountDao mockCompanyAccountDao;

	@Mock
	private ChattyPieAccountService mockChattyPieAccountService;

	@Before
	public void setUp() throws Exception {
		testedAccountService = new CompanyAccountService(mockCompanyAccountDao, mockChattyPieAccountService);
	}

	@Test
	public void testCreateAccountFor_whenAccountRetrievedFromChattyPie_thenPersistCompanyToAccountRelation() throws Exception {
		//Given
		String testCompanyId = "companyId";
		String expectedNewChattyPieAccountId = "newAccountId";
		when(
			mockChattyPieAccountService.createChattyPieAccount()
		).thenReturn(new ChattyPieAccount(expectedNewChattyPieAccountId, 100));

		//When
		CompanyAccount createdCompanyAccount = testedAccountService.createCompanyAccountFor(testCompanyId);

		//Then
		assertThat(createdCompanyAccount.getAccountId()).isEqualTo(expectedNewChattyPieAccountId);
		assertThat(createdCompanyAccount.getId()).isEqualTo(testCompanyId);

		ArgumentCaptor<CompanyAccount> persistedAccountCaptor = ArgumentCaptor.forClass(CompanyAccount.class);
		verify(mockCompanyAccountDao).insert(persistedAccountCaptor.capture());
		CompanyAccount persistedCompanyAccount = persistedAccountCaptor.getValue();

		assertThat(persistedCompanyAccount.getId()).isEqualTo(createdCompanyAccount.getId());
		assertThat(persistedCompanyAccount.getAccountId()).isEqualTo(createdCompanyAccount.getAccountId());
	}

	@Test
	public void testCreateAccountFor_whenErrorOnPersistence_thenTheExceptionContainsTheAccountId() throws Exception {
		//Given
		String testCompanyId = "testCompanyId";
		String testAccountId = "abc";
		when(
			mockChattyPieAccountService.createChattyPieAccount()
		).thenReturn(new ChattyPieAccount(testAccountId, 123));

		doThrow(RuntimeException.class).when(mockCompanyAccountDao).insert(any());

		//Then
		assertThatThrownBy(() ->
			testedAccountService.createCompanyAccountFor(testCompanyId)
		).hasMessage(format("Failed persisting account with accountId=%s in the connector db", testAccountId));
	}

	@Test
	public void testFindExistingCompanyAccountById() {
		//Given
		String testCompanyId = "testCompanyId";
		String expectedAccountId = "expectedAccountId";

		Optional<CompanyAccount> expectedCompanyAccount = Optional.of(
			new CompanyAccount(expectedAccountId, testCompanyId)
		);
		when(mockCompanyAccountDao.readById(testCompanyId))
			.thenReturn(expectedCompanyAccount);

		//When
		Optional<CompanyAccount> existingCompanyAccount = testedAccountService.findExistingCompanyAccountById(testCompanyId);

		//Then
		assertThat(existingCompanyAccount.get().getId()).contains(testCompanyId);
		assertThat(existingCompanyAccount.get().getAccountId()).contains(expectedAccountId);
	}
}
