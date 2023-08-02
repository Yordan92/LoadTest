package com.webint.loadtest.component;

import com.basistech.rosette.api.HttpRosetteAPI;
import org.springframework.stereotype.Component;

@Component
public class HttpRosetteAPIBuilder {

    public HttpRosetteAPI build(String rosetteUrl) {
        HttpRosetteAPI rosetteApi = new HttpRosetteAPI.Builder()
                .url(rosetteUrl)
                .build();
        return rosetteApi;
    }
}
