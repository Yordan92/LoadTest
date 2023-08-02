package com.webint.loadtest.service;

import com.basistech.rosette.api.HttpRosetteAPI;
import com.basistech.rosette.apimodel.*;
import com.webint.loadtest.component.HttpRosetteAPIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import static com.basistech.rosette.api.common.AbstractRosetteAPI.ENTITIES_SERVICE_PATH;
import static com.basistech.rosette.api.common.AbstractRosetteAPI.SENTIMENT_SERVICE_PATH;

@Service
public class HttpRosetteApiService implements NLPService{

    @Autowired
    HttpRosetteAPIBuilder httpRosetteAPIBuilder;
    private static final Logger LOGGER = LoggerFactory.getLogger(RosetteLoadTestService.class);
    Map<String, ArrayBlockingQueue<HttpRosetteAPI>> httpRosetteAPIMapToUrl = new ConcurrentHashMap<>();
    int queueSize = 1;
    @Override
    public EntitiesResponse doEntityExtractionForContent(String rosetteURl, String content) throws IOException, InterruptedException {
//        HttpRosetteAPI httpRosetteAPI = getHttpRosetteAPI(rosetteURl);
        HttpRosetteAPI httpRosetteAPI = httpRosetteAPIBuilder.build(rosetteURl);
        DocumentRequest<EntitiesOptions> request = DocumentRequest.<EntitiesOptions>builder()
                .content(content)
                .build();
        EntitiesResponse response = httpRosetteAPI.perform(ENTITIES_SERVICE_PATH, request, EntitiesResponse.class);
        httpRosetteAPI.close();
        return response;
    }

    private HttpRosetteAPI getHttpRosetteAPI(String rosetteURl) throws InterruptedException {
        ArrayBlockingQueue<HttpRosetteAPI> httpRosetteAPIQueue = httpRosetteAPIMapToUrl.computeIfAbsent(rosetteURl, (k)-> {
            LOGGER.info("Cech will be loaded");
            ArrayBlockingQueue<HttpRosetteAPI> queue = new ArrayBlockingQueue<>(queueSize);
            for (int i = 0; i < queueSize; i++) {
                queue.add(httpRosetteAPIBuilder.build(k));
            }
            return queue;
        });
        HttpRosetteAPI httpRosetteAPI = httpRosetteAPIQueue.take();
        try {
            LOGGER.info(httpRosetteAPI.ping().getExtendedInformation().toString());
        } catch (Exception e) {
            httpRosetteAPI = httpRosetteAPIBuilder.build(rosetteURl);
        }
        httpRosetteAPIMapToUrl.get(rosetteURl).add(httpRosetteAPI);
        return httpRosetteAPI;
//        return httpRosetteAPIBuilder.build(rosetteURl);
    }

    @Override
    public SentimentResponse doSentiment(String rosetteURl, String content) throws IOException {
//        HttpRosetteAPI httpRosetteAPI = httpRosetteAPIMapToUrl.computeIfAbsent(rosetteURl, (k)-> {
//            LOGGER.info("Cech will be loaded");
//            return httpRosetteAPIBuilder.build(k);
//        });
//        HttpRosetteAPI httpRosetteAPI = getHttpRosetteAPI(rosetteURl);
        HttpRosetteAPI httpRosetteAPI = httpRosetteAPIBuilder.build(rosetteURl);
        DocumentRequest<SentimentOptions> request = DocumentRequest.<SentimentOptions>builder()
                .content(content)
                .build();
        SentimentResponse response = httpRosetteAPI.perform(SENTIMENT_SERVICE_PATH, request, SentimentResponse.class);
        httpRosetteAPI.close();
        return response;
    }
}
