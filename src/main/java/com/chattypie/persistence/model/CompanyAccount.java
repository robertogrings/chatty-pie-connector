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

