package com.chattypie;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.appdirect.sdk.ConnectorSdkConfiguration;
import com.appdirect.sdk.appmarket.IsvSpecificAppmarketCredentials;
import com.appdirect.sdk.appmarket.IsvSpecificAppmarketCredentialsSupplier;

@Configuration
@Import(ConnectorSdkConfiguration.class)
public class RootConfiguration {
    
    @Bean
    public IsvSpecificAppmarketCredentialsSupplier credentialsSupplier() {
        return () -> new IsvSpecificAppmarketCredentials("sample", "sample");
    }
}

