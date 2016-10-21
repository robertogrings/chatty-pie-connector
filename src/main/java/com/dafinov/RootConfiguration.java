package com.dafinov;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.appdirect.sdk.ConnectorSdkConfiguration;
import com.appdirect.sdk.isv.IsvSpecificMarketplaceCredentials;
import com.appdirect.sdk.isv.IsvSpecificMarketplaceCredentialsSupplier;

@Configuration
@Import(ConnectorSdkConfiguration.class)
public class RootConfiguration {
    
    @Bean
    public IsvSpecificMarketplaceCredentialsSupplier credentialsSupplier() {
        return () -> new IsvSpecificMarketplaceCredentials("sample", "sample");
    }
}

