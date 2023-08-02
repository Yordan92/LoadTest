package com.webint.loadtest.dto;

import java.util.Deque;

public class ResourceStatistic {
    int timesSendForEnrichment;
    long wordsInFile;

    long numberOfFailures = 0;
    MinMaxAvgDTO<Long> pendingRequestsMadeWhenFailureHappens = new MinMaxAvgDTO<>(0L);

    MinMaxAvgDTO<Long> timeTakenFromProcess = new MinMaxAvgDTO<>(0L);


    public int getTimesSendForEnrichment() {
        return timesSendForEnrichment;
    }

    public void setTimesSendForEnrichment(int timesSendForEnrichment) {
        this.timesSendForEnrichment = timesSendForEnrichment;
    }

    public long getWordsInFile() {
        return wordsInFile;
    }

    public void setWordsInFile(long wordsInFile) {
        this.wordsInFile = wordsInFile;
    }

    public MinMaxAvgDTO<Long> getTimeTakenFromProcess() {
        return timeTakenFromProcess;
    }

    public void setTimeTakenFromProcess(MinMaxAvgDTO<Long> timeTakenFromProcess) {
        this.timeTakenFromProcess = timeTakenFromProcess;
    }

    public long getNumberOfFailures() {
        return numberOfFailures;
    }

    public void setNumberOfFailures(long numberOfFailures) {
        this.numberOfFailures = numberOfFailures;
    }

    public MinMaxAvgDTO<Long> getPendingRequestsMadeWhenFailureHappens() {
        return pendingRequestsMadeWhenFailureHappens;
    }

    public void setPendingRequestsMadeWhenFailureHappens(MinMaxAvgDTO<Long> pendingRequestsMadeWhenFailureHappens) {
        this.pendingRequestsMadeWhenFailureHappens = pendingRequestsMadeWhenFailureHappens;
    }
}
