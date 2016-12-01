package com.chattypie.persistence.model;

import javax.annotation.Generated;

/**
 * CompanyAccount is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class CompanyAccount {

    public CompanyAccount() {
    }

    public CompanyAccount(String accountId, String id) {
        this.accountId = accountId;
        this.id = id;
    }

    private String accountId;

    private String id;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
         return "accountId = " + accountId + ", id = " + id;
    }

}

