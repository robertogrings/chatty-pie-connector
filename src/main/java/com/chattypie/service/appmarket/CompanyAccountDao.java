package com.chattypie.service.appmarket;

import static com.chattypie.persistence.model.QCompanyAccount.companyAccount;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import com.chattypie.persistence.model.CompanyAccount;
import com.querydsl.sql.SQLQueryFactory;

@RequiredArgsConstructor
public class CompanyAccountDao {

	private final SQLQueryFactory queryFactory;

	public void insert(CompanyAccount accountToPersist) {
		queryFactory
			.insert(companyAccount)
			.populate(accountToPersist)
			.executeWithKey(CompanyAccount.class);
	}

	public Optional<CompanyAccount> readById(String requestedCompanyId) {
		return Optional.ofNullable(
			queryFactory
			.selectFrom(companyAccount)
			.where(companyAccount.id.eq(requestedCompanyId))
			.fetchOne()
		);
	}
}
