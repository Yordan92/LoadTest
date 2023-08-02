package com.webint.loadtest.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StatisticsDTO {
    private MachineLoadStatisticsDTO machineLoadStatisticsDTO;

    private double wordsPerSecond;
    private double charsPerSecond;

    private int numberOfThreads;

    private long testTookTime;

    private long numberOfRequests;
    private Set<String> testExecutedForLanguages;

    private Map<String, ResourceStatistic> resourceStatisticMap = new HashMap<>();

    public StatisticsDTO() {}
    public StatisticsDTO(int numberOfThreads, Set<String> testExecutedForLanguages) {
        this.numberOfThreads = numberOfThreads;
        this.testExecutedForLanguages = testExecutedForLanguages;
    }

    public long getTestTookTime() {
        return testTookTime;
    }

    public void setTestTookTime(long testTookTime) {
        this.testTookTime = testTookTime;
    }



    public double getWordsPerSecond() {
        return wordsPerSecond;
    }

    public void setWordsPerSecond(double wordsPerSecond) {
        this.wordsPerSecond = wordsPerSecond;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public Set<String> getTestExecutedForLanguages() {
        return testExecutedForLanguages;
    }

    public void setTestExecutedForLanguages(Set<String> testExecutedForLanguages) {
        this.testExecutedForLanguages = testExecutedForLanguages;
    }

    public Map<String, ResourceStatistic> getResourceStatisticMap() {
        return resourceStatisticMap;
    }

    public void setResourceStatisticMap(Map<String, ResourceStatistic> resourceStatisticMap) {
        this.resourceStatisticMap = resourceStatisticMap;
    }

    public MachineLoadStatisticsDTO getMachineLoadStatisticsDTO() {
        return machineLoadStatisticsDTO;
    }

    public void setMachineLoadStatisticsDTO(MachineLoadStatisticsDTO machineLoadStatisticsDTO) {
        this.machineLoadStatisticsDTO = machineLoadStatisticsDTO;
    }

    public long getNumberOfRequests() {
        return numberOfRequests;
    }

    public void setNumberOfRequests(long numberOfRequests) {
        this.numberOfRequests = numberOfRequests;
    }

    public double getCharsPerSecond() {
        return charsPerSecond;
    }

    public void setCharsPerSecond(double charsPerSecond) {
        this.charsPerSecond = charsPerSecond;
    }

    public void addResourceStatistics(String filename, int wordsInFile, long time) {
        ResourceStatistic resourceStatistic;
        if (resourceStatisticMap.containsKey(filename)) {
            resourceStatistic = resourceStatisticMap.get(filename);
        } else {
            resourceStatistic = new ResourceStatistic();
            resourceStatisticMap.put(filename, resourceStatistic);
        }
        int times = resourceStatistic.getTimesSendForEnrichment();
        resourceStatistic.setTimesSendForEnrichment(times + 1);
        resourceStatistic.setWordsInFile(wordsInFile);
        MinMaxAvgDTO<Long> timeTaken = resourceStatistic.getTimeTakenFromProcess();
        timeTaken.addMinMax(time);
        timeTaken.setSum(timeTaken.getSum() + time);
        timeTaken.setTimes(timeTaken.getTimes() + 1);
        timeTaken.setAvg(timeTaken.getSum() / timeTaken.getTimes());
    }

    public void addFailureStatistics(String filename, long numberOfPendingRequests) {
        ResourceStatistic resourceStatistic;
        if (resourceStatisticMap.containsKey(filename)) {
            resourceStatistic = resourceStatisticMap.get(filename);
        } else {
            resourceStatistic = new ResourceStatistic();
            resourceStatisticMap.put(filename, resourceStatistic);
        }
        resourceStatistic.setNumberOfFailures(resourceStatistic.getNumberOfFailures() + 1);

        resourceStatistic.getPendingRequestsMadeWhenFailureHappens().addMinMax(numberOfPendingRequests);
    }
}
