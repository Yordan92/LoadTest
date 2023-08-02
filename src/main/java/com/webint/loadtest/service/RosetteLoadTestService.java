package com.webint.loadtest.service;

import com.webint.loadtest.dto.ProbeZipContent;
import com.webint.loadtest.iterator.ProbeZipIterator;
import com.webint.loadtest.iterator.ResourceIterator;
import com.webint.loadtest.dto.RosetteResponseDTO;
import com.webint.loadtest.dto.StatisticsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RosetteLoadTestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RosetteLoadTestService.class);

    boolean TEST_IS_RUNNING = true;

    @Autowired
    LoadTestResourceService loadTestResourceService;
    @Autowired
    HttpRosetteApiService httpRosetteApiService;

    @Autowired
    HttpRosetteApiServiceUsingRest httpRosetteApiServiceUsingRest;

    Map<String, HttpRosetteEEApiCaller> mapResourceNameToEECaller = new HashMap<>();
    Map<String, HttpRosetteSentimentApiCaller> mapResourceNameToSentimentCaller = new HashMap<>();


    public StatisticsDTO loadTestWithFiles(String rosetteUrl, String path, int numberOfThreads, Set<String> languages, Double executionTimeInMinutes, boolean shouldPreload, boolean doEntityExtraction, boolean doSentiment, boolean useRest) throws IOException, ExecutionException, InterruptedException {

        TEST_IS_RUNNING = true;
        Semaphore semaphore = new Semaphore(numberOfThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        LinkedBlockingQueue<RosetteResponseDTO> rosetteResponseWrappers = new LinkedBlockingQueue<>();
        Future<StatisticsDTO> statisticFuture = startStatCalculator(rosetteResponseWrappers);

        LocalDateTime startTime = LocalDateTime.now();
        ResourceIterator resourceIterator = loadTestResourceService.loadResourcesForLanguages(path, languages, shouldPreload);
        NLPService nlpService = useRest ? httpRosetteApiServiceUsingRest : httpRosetteApiService;
        while(Duration.between(startTime, LocalDateTime.now()).getSeconds() <= executionTimeInMinutes * 60L) {
            String[] resourceNameAndContent = resourceIterator.next();
            if (doEntityExtraction) {
//                HttpRosetteEEApiCaller httpRosetteApiCaller = mapResourceNameToEECaller.computeIfAbsent(resourceNameAndContent[0], (absolutePathToResource) -> new HttpRosetteEEApiCaller(resourceNameAndContent[1], httpRosetteApiService, rosetteUrl, absolutePathToResource));
                HttpRosetteEEApiCaller httpRosetteApiCaller = new HttpRosetteEEApiCaller(resourceNameAndContent[1], nlpService, rosetteUrl, resourceNameAndContent[0]);
                CompletableFuture
                        .supplyAsync(httpRosetteApiCaller, executorService)
                        .thenApply(rosetteResponseWrapper -> {
                            rosetteResponseWrappers.add(rosetteResponseWrapper);
                            semaphore.release();
                            return rosetteResponseWrapper;
                        });
                semaphore.acquire();
            }
            if (doSentiment) {
//                HttpRosetteSentimentApiCaller httpRosetteApiCaller = mapResourceNameToSentimentCaller.computeIfAbsent(resourceNameAndContent[0], (absolutePathToResource) -> new HttpRosetteSentimentApiCaller(resourceNameAndContent[1], httpRosetteApiService, rosetteUrl, absolutePathToResource));
                HttpRosetteSentimentApiCaller httpRosetteApiCaller = new HttpRosetteSentimentApiCaller(resourceNameAndContent[1], nlpService, rosetteUrl, resourceNameAndContent[0]);
                CompletableFuture
                        .supplyAsync(httpRosetteApiCaller, executorService)
                        .thenApply(rosetteResponseWrapper -> {
                            rosetteResponseWrappers.add(rosetteResponseWrapper);
                            semaphore.release();
                            return rosetteResponseWrapper;
                        });
                semaphore.acquire();
            }
        }
        TEST_IS_RUNNING = false;
        StatisticsDTO statisticsDTO = null;
        try {
            statisticsDTO = statisticFuture.get();
            statisticsDTO.setTestTookTime((Duration.between(startTime, LocalDateTime.now()).toMillis()));
            statisticsDTO.setNumberOfThreads(numberOfThreads);
            statisticsDTO.setTestExecutedForLanguages(languages);

        } catch (Exception exception) {
            LOGGER.error("Problem getting statistics", exception);
        }
        return statisticsDTO;
    }
    public StatisticsDTO enrichZipsComingFromDistributor(String rosetteUrl, String path, int numberOfThreads, boolean useRest) throws IOException, ExecutionException, InterruptedException {

        TEST_IS_RUNNING = true;
        Semaphore semaphore = new Semaphore(numberOfThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        LinkedBlockingQueue<RosetteResponseDTO> rosetteResponseWrappers = new LinkedBlockingQueue<>();

        LocalDateTime startTime = LocalDateTime.now();
        ProbeZipIterator resourceIterator = loadTestResourceService.loadProbeZipsForEnrichment(path);
        NLPService nlpService = useRest ? httpRosetteApiServiceUsingRest : httpRosetteApiService;
        AtomicInteger numberOfRequests = new AtomicInteger(0);
        Future<StatisticsDTO> statisticFuture = startStatCalculator(rosetteResponseWrappers);
        while(resourceIterator.hasNext()) {
            ProbeZipContent probeZipContent = resourceIterator.next();
            for (String text : probeZipContent.getAllEntities()) {
                numberOfRequests.addAndGet(2);
                HttpRosetteEEApiCaller httpRosetteApiCaller = new HttpRosetteEEApiCaller(text, nlpService, rosetteUrl, probeZipContent.getFilename());
                CompletableFuture
                        .supplyAsync(httpRosetteApiCaller, executorService)
                        .thenApply(rosetteResponseWrapper -> {
                            rosetteResponseWrappers.add(rosetteResponseWrapper);
                            semaphore.release();
                            numberOfRequests.getAndDecrement();
                            return rosetteResponseWrapper;
                        });

                HttpRosetteSentimentApiCaller httpRosetteSentimentApiCaller = new HttpRosetteSentimentApiCaller(text, nlpService, rosetteUrl, probeZipContent.getFilename());;
                CompletableFuture
                        .supplyAsync(httpRosetteSentimentApiCaller, executorService)
                        .thenApply(rosetteResponseWrapper -> {
                            rosetteResponseWrappers.add(rosetteResponseWrapper);
                            semaphore.release();
                            numberOfRequests.getAndDecrement();
                            return rosetteResponseWrapper;
                        });
            }

        }
        while(numberOfRequests.get() > 0) {
        }
        System.out.println("Test is going to complete");
        TEST_IS_RUNNING = false;
        StatisticsDTO statisticsDTO = null;
        try {
            statisticsDTO = statisticFuture.get();
            statisticsDTO.setTestTookTime((Duration.between(startTime, LocalDateTime.now()).toMillis()));
            statisticsDTO.setNumberOfThreads(numberOfThreads);

        } catch (Exception exception) {
            LOGGER.error("Problem getting statistics", exception);
        }
        return statisticsDTO;
    }

    private Future<StatisticsDTO> startStatCalculator(LinkedBlockingQueue<RosetteResponseDTO> rosetteResponseWrappers) {
        ExecutorService threadSomething = Executors.newFixedThreadPool(1);
        Future calculateStatistics = threadSomething.submit(() -> {
            StatisticsDTO statisticsDTO = new StatisticsDTO();
            try {
                long numberOfTokens = 0;
                long numberOfChars = 0;
                long milliseconds = 0;
                long numberOfRequests = 0;
                while(TEST_IS_RUNNING) {
                    RosetteResponseDTO rosetteResponseDTO = rosetteResponseWrappers.poll(1, TimeUnit.SECONDS);
                    if (rosetteResponseDTO == null) {
                        continue;
                    }
                    boolean success = rosetteResponseDTO.getFailure() == null || rosetteResponseDTO.getFailure().isEmpty();
                    numberOfRequests++;
                    numberOfTokens += rosetteResponseDTO.getNumberOfWords();
                    numberOfChars += rosetteResponseDTO.getNumberOfChars();
                    long timeNeededForRosetteResponse =  rosetteResponseDTO.getEndTime() - rosetteResponseDTO.getStartTime();
                    milliseconds += timeNeededForRosetteResponse;
                    if (success) {
                        statisticsDTO.addResourceStatistics(rosetteResponseDTO.getFilePath(), rosetteResponseDTO.getNumberOfWords(), timeNeededForRosetteResponse);
                    } else {
                        statisticsDTO.addFailureStatistics(rosetteResponseDTO.getFilePath(), rosetteResponseWrappers.size());
                    }

                }
                statisticsDTO.setWordsPerSecond(numberOfTokens * 1.0 / milliseconds * 1000);
                statisticsDTO.setCharsPerSecond(numberOfChars *  1.0 / milliseconds * 1000);
                statisticsDTO.setNumberOfRequests(numberOfRequests);
            } catch (InterruptedException  e) {
                LOGGER.error("Problem getting data from queue", e);
            }
            return statisticsDTO;
        });
        return calculateStatistics;
    }
}
