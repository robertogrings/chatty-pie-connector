package com.chattypie.processor;

import org.springframework.stereotype.Component;

import com.appdirect.sdk.appmarket.AppmarketEventProcessor;
import com.appdirect.sdk.appmarket.api.type.EventType;
import com.appdirect.sdk.appmarket.api.vo.APIResult;
import com.appdirect.sdk.appmarket.api.vo.EventInfo;

@Component
public class SubscriptionOrderProcessor implements AppmarketEventProcessor {
    @Override
    public boolean supports(EventType eventType) {
        return true;
    }

    @Override
    public APIResult process(EventInfo eventInfo, String s) {
        return new APIResult(true, "I received and treated an event of type " + eventInfo.getType());
    }
}
