package com.webint.loadtest.service;

import com.basistech.rosette.api.HttpRosetteAPI;
import com.basistech.rosette.api.HttpRosetteAPIException;
import com.basistech.rosette.apimodel.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webint.loadtest.component.HttpRosetteAPIBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;

import static com.basistech.rosette.api.common.AbstractRosetteAPI.ENTITIES_SERVICE_PATH;
import static com.basistech.rosette.api.common.AbstractRosetteAPI.SENTIMENT_SERVICE_PATH;
import static java.net.HttpURLConnection.HTTP_OK;

@Service
public class HttpRosetteApiServiceUsingRest implements NLPService{

    @Autowired
    HttpRosetteAPIBuilder httpRosetteAPIBuilder;
    private static final Logger LOGGER = LoggerFactory.getLogger(RosetteLoadTestService.class);
    Map<String, ArrayBlockingQueue<HttpRosetteAPI>> httpRosetteAPIMapToUrl = new ConcurrentHashMap<>();
    int queueSize = 1;

    private ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);;

    @Override
    public EntitiesResponse doEntityExtractionForContent(String rosetteURl, String content) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(rosetteURl  + ENTITIES_SERVICE_PATH);
        Map<String, String> contentMap = Collections.singletonMap("content", content);
        HttpEntity httpEntity = new StringEntity(objectMapper.writeValueAsString(contentMap), ContentType.APPLICATION_JSON);
        post.setEntity(httpEntity);

        try {
            HttpResponse response = client.execute(post);
            EntitiesResponse entitiesResponse = getResponse(response, EntitiesResponse.class);
            if (entitiesResponse == null || entitiesResponse.getExtendedInformation() == null) {
                LOGGER.info("convertion failed");
            }
            return entitiesResponse;
        } catch (Exception ex) {

            LOGGER.error("Response from server:{}", ex.getMessage(), ex);
            throw ex;
        }

    }

    @Override
    public SentimentResponse doSentiment(String rosetteURl, String content) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(rosetteURl + SENTIMENT_SERVICE_PATH);
        Map<String, String> contentMap = Collections.singletonMap("content", content);
        HttpEntity httpEntity = new StringEntity(objectMapper.writeValueAsString(contentMap), ContentType.APPLICATION_JSON);
        post.setEntity(httpEntity);

        try {
            HttpResponse response = client.execute(post);
            SentimentResponse entitiesResponse = getResponse(response, SentimentResponse.class);
            if (entitiesResponse == null || entitiesResponse.getExtendedInformation() == null) {
                LOGGER.info("convertion failed");
            }
            return entitiesResponse;
        } catch (Exception ex) {
            LOGGER.error("Response from server:{}", ex.getMessage(), ex);
            throw ex;
        }
    }


    private <T extends Response> T getResponse(HttpResponse httpResponse, Class<T> clazz) throws IOException, HttpRosetteAPIException {
        int status = httpResponse.getStatusLine().getStatusCode();
        String encoding = headerValueOrNull(httpResponse.getFirstHeader(HttpHeaders.CONTENT_ENCODING));

        try (
            InputStream stream = httpResponse.getEntity().getContent();
            InputStream inputStream = "gzip".equalsIgnoreCase(encoding) ? new GZIPInputStream(stream) : stream) {
            String ridHeader = headerValueOrNull(httpResponse.getFirstHeader("X-RosetteAPI-DocumentRequest-Id"));
            if (HTTP_OK != status) {
                String ecHeader = headerValueOrNull(httpResponse.getFirstHeader("X-RosetteAPI-Status-Code"));
                String emHeader = headerValueOrNull(httpResponse.getFirstHeader("X-RosetteAPI-Status-Message"));
                String responseContentType = headerValueOrNull(httpResponse.getFirstHeader(HttpHeaders.CONTENT_TYPE));
                if ("application/json".equals(responseContentType)) {
                    ErrorResponse errorResponse = objectMapper.readValue(inputStream, ErrorResponse.class);
                    if (ridHeader != null) {
                    }
                    if (ecHeader != null) {
                        errorResponse.setCode(ecHeader);
                    }
                    if (429 == status) {
                        String concurrencyMessage = "You have exceeded your plan's limit on concurrent calls. "
                                + "This could be caused by multiple processes or threads making Rosette API calls in parallel, "
                                + "or if your httpClient is configured with higher concurrency than your plan allows.";
                        if (emHeader == null) {
                            emHeader = concurrencyMessage;
                        } else {
                            emHeader = concurrencyMessage + System.lineSeparator() + emHeader;
                        }
                    }
                    if (emHeader != null) {
                        errorResponse.setMessage(emHeader);
                    }
                    throw new HttpRosetteAPIException(errorResponse, status);
                } else {
                    String errorContent;
                    if (inputStream != null) {
                        byte[] content = IOUtils.toByteArray(inputStream);
                        errorContent = new String(content, "utf-8");
                    } else {
                        errorContent = "(no body)";
                    }
                    // something not from us at all
                    throw new HttpRosetteAPIException("Invalid error response (not json)",
                            ErrorResponse.builder().code("invalidErrorResponse").message(errorContent).build(), status);
                }
            } else {
                T response = objectMapper.readValue(inputStream, clazz);
                for (Header header : httpResponse.getAllHeaders()) {
                    response.addExtendedInformation(header.getName(), header.getValue());

                }
                return response;
            }
        }
    }
    private String headerValueOrNull(Header header) {
        if (header == null) {
            return null;
        } else {
            return header.getValue();
        }
    }
}
