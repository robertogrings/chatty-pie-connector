package com.chattypie.service.chattypie.greeting;

import com.appdirect.sdk.appmarket.events.CompanyInfo;

public interface GreetingService {
	void sendNewCompanyGreeting(CompanyInfo company);
}
