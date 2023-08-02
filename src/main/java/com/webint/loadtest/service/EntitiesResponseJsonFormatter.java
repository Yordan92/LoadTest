package com.webint.loadtest.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class EntitiesResponseJsonFormatter<T> implements EntitiesResponseFormatter<T> {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public String format(T entitiesResponse) throws JsonProcessingException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entitiesResponse);
    }
}
