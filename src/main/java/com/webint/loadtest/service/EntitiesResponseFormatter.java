package com.webint.loadtest.service;

import com.basistech.rosette.apimodel.EntitiesResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface EntitiesResponseFormatter<T> {
    public String format(T response) throws Exception;
}
