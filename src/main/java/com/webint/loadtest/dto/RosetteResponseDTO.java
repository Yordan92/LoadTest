package com.webint.loadtest.dto;

import com.basistech.rosette.apimodel.EntitiesResponse;
import com.basistech.rosette.apimodel.SentimentResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RosetteResponseDTO {
    private int numberOfWords;
    private long numberOfChars;
    private long startTime;
    private long endTime;
    private String filePath;
    private  EntitiesResponse entitiesResponse;

    private SentimentResponse sentimentResponse;

    private String failure;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public RosetteResponseDTO(EntitiesResponse entitiesResponse, long startTime, int numberOfWords, String filePath, String failure, long numberOfChars) {
        this.entitiesResponse = entitiesResponse;
        this.startTime = startTime;
        this.numberOfWords = numberOfWords;
        this.endTime = System.currentTimeMillis();
        this.filePath = filePath;
        this.failure = failure;
        this.numberOfChars = numberOfChars;

    }

    public RosetteResponseDTO(long startTime, int numberOfWords, String filePath, String failure, long numberOfChars) {
        this.startTime = startTime;
        this.numberOfWords = numberOfWords;
        this.endTime = System.currentTimeMillis();
        this.filePath = filePath;
        this.failure = failure;
        this.numberOfChars = numberOfChars;

    }
    public RosetteResponseDTO(SentimentResponse sentimentResponse, long startTime, int numberOfWords, String filePath, String failure, long numberOfChars) {
        this.sentimentResponse = sentimentResponse;
        this.startTime = startTime;
        this.numberOfWords = numberOfWords;
        this.endTime = System.currentTimeMillis();
        this.filePath = filePath;
        this.failure = failure;
        this.numberOfChars = numberOfChars;
    }
    public String toJson() throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(this);
    }

    public int getNumberOfWords() {
        return numberOfWords;
    }

    public void setNumberOfWords(int numberOfWords) {
        this.numberOfWords = numberOfWords;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFailure() {
        return failure;
    }

    public void setFailure(String failure) {
        this.failure = failure;
    }

    public long getNumberOfChars() {
        return numberOfChars;
    }

    public void setNumberOfChars(long numberOfChars) {
        this.numberOfChars = numberOfChars;
    }

    @Override
    public String toString() {
        return "RosetteResponseWrapper{" +
                "numberOfWords=" + numberOfWords +
                ", timeTaken in seconds=" + (endTime - startTime) / 1000 +
                ", entitiesResponseExtendedInformation=" + (entitiesResponse != null ? entitiesResponse.getExtendedInformation() : failure) +
                '}';
    }
}
