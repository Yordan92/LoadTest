package com.webint.loadtest.service;

import com.webint.loadtest.command.RosetteLoadTest;
import com.webint.loadtest.dto.MachineLoadStatisticsDTO;
import com.webint.loadtest.dto.StatisticsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.shell.standard.ShellOption;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Service
public class CommandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandService.class);

    @Autowired
    private HttpRosetteApiService rosetteApiService;

    @Autowired
    EntitiesResponseJsonFormatter entitiesResponseJsonFormatter;

    @Autowired
    EntitiesResponseCSVFormatter entitiesResponseCSVFormatter;

    @Autowired
    private RosetteLoadTestService rosetteLoadTestService;

    @Autowired
    private RemoteMachineLoadMonitoringService remoteMachineLoadMonitoringService;
    public String loadTestEE(String rosetteUrl,
                             String path,
                             String numberOfThreads,
                             String timeToExecute,
                             String exportMachineStatisticsUrl,
                             boolean shouldFilesBeLoadedIntoMemory,
                             boolean doEntityEnrichment ,
                             boolean doSentiment,
                             String[] languages,
                             boolean useRest) throws IOException, ExecutionException, InterruptedException {
        LOGGER.info("Rosette on url:{} will be called for all files on path:{}", rosetteUrl, path);
        MachineLoadStatisticsDTO machineLoadStatisticsDTO = new MachineLoadStatisticsDTO();
        remoteMachineLoadMonitoringService.start(exportMachineStatisticsUrl, machineLoadStatisticsDTO);
        Set<String> languagesSet;
        if (languages == null || languages.length == 0) {
            languagesSet = Collections.emptySet();
        } else {
            languagesSet = new HashSet<>(Arrays.asList(languages));
        }
        StatisticsDTO statisticsDTO = rosetteLoadTestService.loadTestWithFiles(rosetteUrl, path, Integer.parseInt(numberOfThreads), languagesSet, Double.parseDouble(timeToExecute), shouldFilesBeLoadedIntoMemory, doEntityEnrichment, doSentiment, useRest);
        remoteMachineLoadMonitoringService.stop();
        statisticsDTO.setMachineLoadStatisticsDTO(machineLoadStatisticsDTO);
        return entitiesResponseJsonFormatter.format(statisticsDTO);
    }

    public StatisticsDTO getStatisticsDTO(String rosetteUrl,
                             String path,
                             String numberOfThreads,
                             String timeToExecute,
                             String exportMachineStatisticsUrl,
                             boolean shouldFilesBeLoadedIntoMemory,
                             boolean doEntityEnrichment ,
                             boolean doSentiment,
                             String[] languages) throws IOException, ExecutionException, InterruptedException {
        LOGGER.info("Rosette on url:{} will be called for all files on path:{}", rosetteUrl, path);
        MachineLoadStatisticsDTO machineLoadStatisticsDTO = new MachineLoadStatisticsDTO();
        remoteMachineLoadMonitoringService.start(exportMachineStatisticsUrl, machineLoadStatisticsDTO);
        Set<String> languagesSet;
        if (languages == null || languages.length == 0) {
            languagesSet = Collections.emptySet();
        } else {
            languagesSet = new HashSet<>(Arrays.asList(languages));
        }
        StatisticsDTO statisticsDTO = rosetteLoadTestService.loadTestWithFiles(rosetteUrl, path, Integer.parseInt(numberOfThreads), languagesSet, Double.parseDouble(timeToExecute), shouldFilesBeLoadedIntoMemory, doEntityEnrichment, doSentiment, true);
        remoteMachineLoadMonitoringService.stop();
        statisticsDTO.setMachineLoadStatisticsDTO(machineLoadStatisticsDTO);
        return statisticsDTO;
    }

    public StatisticsDTO doNLPOnProbeZips(String rosetteUrl,
                                          String path,
                                          String numberOfThreads,
                                          String exportMachineStatisticsUrl) throws IOException, ExecutionException, InterruptedException {
        LOGGER.info("Rosette on url:{} will be called for all files on path:{}", rosetteUrl, path);
        MachineLoadStatisticsDTO machineLoadStatisticsDTO = new MachineLoadStatisticsDTO();
        remoteMachineLoadMonitoringService.start(exportMachineStatisticsUrl, machineLoadStatisticsDTO);
        rosetteLoadTestService.enrichZipsComingFromDistributor(rosetteUrl, path, Integer.parseInt(numberOfThreads), true);
        remoteMachineLoadMonitoringService.stop();
        return null;
    }
}
