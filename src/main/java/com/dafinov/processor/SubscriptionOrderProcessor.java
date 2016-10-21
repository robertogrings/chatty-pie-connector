package com.dafinov.processor;

import org.springframework.stereotype.Component;

import com.appdirect.sdk.isv.api.model.type.EventType;
import com.appdirect.sdk.isv.api.model.vo.APIResult;
import com.appdirect.sdk.isv.api.model.vo.EventInfo;
import com.appdirect.sdk.isv.service.processor.IsvEventProcessor;

@Component
public class SubscriptionOrderProcessor implements IsvEventProcessor {
    @Override
    public boolean supports(EventType eventType) {
        return true;
    }

    @Override
    public APIResult process(EventInfo eventInfo, String s) {
        return new APIResult(true, "I received and treated an event of type " + eventInfo.getType());
    }
}
