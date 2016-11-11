package com.chattypie.service.appmarket;

import static java.lang.String.format;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.chattypie.persistence.model.CompanyAccount;
import com.chattypie.service.chattypie.account.ChattyPieAccount;
import com.chattypie.service.chattypie.account.ChattyPieAccountService;

@Slf4j
@RequiredArgsConstructor
public class CompanyAccountService {

	private final CompanyAccountDao companyAccountDao;
	private final ChattyPieAccountService chattyPieAccountService;

	public CompanyAccount createCompanyAccountFor(String companyId) {
		ChattyPieAccount newChattyPieAccount = chattyPieAccountService.createChattyPieAccount();

		try {
			companyAccountDao.insert(
				new CompanyAccount(newChattyPieAccount.getId(), companyId)
			);
		} catch (Exception e) {
			String errorMessage = format("Failed persisting account with accountId=%s in the connector db", newChattyPieAccount.getId());
			log.error(errorMessage, newChattyPieAccount.getId());
			throw new DatabaseException(errorMessage, e);
		}

		return new CompanyAccount(newChattyPieAccount.getId(), companyId);
	}

	public Optional<CompanyAccount> findExistingCompanyAccountById(String companyId) {
		return companyAccountDao.readById(companyId);
	}
}
