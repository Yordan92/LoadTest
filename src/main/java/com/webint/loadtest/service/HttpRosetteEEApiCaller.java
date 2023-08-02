package com.webint.loadtest.service;

import com.basistech.rosette.api.HttpRosetteAPIException;
import com.basistech.rosette.apimodel.EntitiesResponse;
import com.webint.loadtest.dto.RosetteResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class HttpRosetteEEApiCaller implements Callable<RosetteResponseDTO>, Supplier<RosetteResponseDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRosetteEEApiCaller.class);

    private String textToEnrich;
   private NLPService httpRosetteApiService;
   private String rosetteURl;
   private String resourcePаth;

    public HttpRosetteEEApiCaller(String textToEnrich, NLPService httpRosetteApiService, String rosetteURl, String resourcePath) {
        this.textToEnrich = textToEnrich;
        this.httpRosetteApiService = httpRosetteApiService;
        this.rosetteURl = rosetteURl;
        this.resourcePаth = resourcePath;
    }
    @Override
    public RosetteResponseDTO call() {
        long startTime = System.currentTimeMillis();
        try {
            int numberOfWords = 0;
            EntitiesResponse  entitiesResponse = this.httpRosetteApiService.doEntityExtractionForContent(this.rosetteURl, this.textToEnrich);
            String numberOfTokens = (String) entitiesResponse.getExtendedInformation().get("X-RosetteAPI-Stats-AdmTokenCount");
            numberOfWords = Integer.parseInt(numberOfTokens);
            return new RosetteResponseDTO(entitiesResponse, startTime, numberOfWords, resourcePаth, null, this.textToEnrich.length());
        } catch (HttpRosetteAPIException exception) {
            LOGGER.error("Error during parsing response from Rosette, {}", exception.getErrorResponse().getMessage(), exception);
            return new RosetteResponseDTO(startTime, 0, resourcePаth, exception.getErrorResponse().getMessage(), this.textToEnrich.length());
        } catch (Exception exception) {
            LOGGER.error("Error during parsing response from Rosette", exception);
            String message = exception.getMessage();
            if (message == null || message.isEmpty()) {
                message = exception.toString();
            }
            return new RosetteResponseDTO(startTime, 0, resourcePаth, message, this.textToEnrich.length());
        }
    }

    @Override
    public RosetteResponseDTO get() {
        return this.call();
    }
}
