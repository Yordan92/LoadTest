package com.webint.loadtest.service;

import com.basistech.rosette.apimodel.EntitiesResponse;
import com.basistech.rosette.apimodel.SentimentResponse;

import java.io.IOException;

public interface NLPService {

    EntitiesResponse doEntityExtractionForContent(String rosetteURl, String content) throws Exception;
    SentimentResponse doSentiment(String rosetteURl, String content) throws Exception;
}
