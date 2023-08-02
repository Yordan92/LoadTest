package com.webint.loadtest.command;

import com.basistech.rosette.apimodel.EntitiesResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Charsets;
import com.webint.loadtest.dto.MachineLoadStatisticsDTO;
import com.webint.loadtest.dto.RosetteResponseDTO;
import com.webint.loadtest.dto.StatisticsDTO;
import com.webint.loadtest.service.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import javax.validation.constraints.Pattern;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

@ShellComponent
public class RosetteLoadTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RosetteLoadTest.class);

    @Autowired
    CommandService commandService;
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

    @ShellMethod(key = "test-entity-extraction", value = "test-entity-extraction http://<rosetteIp>:<port>/rest/v1 <content> to be enriched")
    public String testEntityExtraction(@ShellOption(defaultValue = "localhost:8181", help="Url where rosette standalone is installed") @Pattern(regexp = "https?:\\/\\/.+\\/rest\\/v1") String rosetteUrl,
                             @ShellOption(help="Text on which nlp will be performed") String text) throws JsonProcessingException {
        LOGGER.info("Rosette on url:{} will be called with text:{}", rosetteUrl, text);
        HttpRosetteEEApiCaller httpRosetteEEApiCaller = new HttpRosetteEEApiCaller(text, rosetteApiService,rosetteUrl, "manual");
        RosetteResponseDTO entitiesResponse = httpRosetteEEApiCaller.call();
        return entitiesResponseJsonFormatter.format(entitiesResponse);
    }

    @ShellMethod(key = "load-test-entity-extraction", value = "load-test-entity-extraction http://<rosetteIp>:<port>/rest/v1 <content> to be enriched")
    public String loadTestEntityExtraction(@ShellOption(defaultValue = "localhost:8181", help="Url where rosette standalone is installed") @Pattern(regexp = "https?:\\/\\/.+\\/rest\\/v1") String rosetteUrl,
                                       @ShellOption(help="Path to data source to send") String path,
                                       @ShellOption(help="Number of threads to work asynchronously to send data") String numberOfThreads,
                                       @ShellOption(help="Time to execute the test in minutes") String timeToExecute,
                                       @ShellOption(help="Url which gets machine statistics during entity extraction") String exportMachineStatisticsUrl,
                                       @ShellOption(help="If files need to be preloaded into memory") boolean shouldFilesBeLoadedIntoMemory,
                                       @ShellOption(help="If files need to be preloaded into memory") boolean doEntityEnrichment ,
                                       @ShellOption(help="If files need to be preloaded into memory") boolean doSentiment,
                                           @ShellOption(help="If files need to be preloaded into memory") boolean useRest,
                                       @ShellOption(help="For languages", defaultValue="") String[] languages) throws IOException, ExecutionException, InterruptedException {

        LOGGER.info("Rosette on url:{} will be called for all files on path:{}", rosetteUrl, path);
        return commandService.loadTestEE(rosetteUrl,path, numberOfThreads, timeToExecute, exportMachineStatisticsUrl, shouldFilesBeLoadedIntoMemory, doEntityEnrichment, doSentiment, languages, useRest);
    }

    @ShellMethod(key = "load-test-entity-extraction-extraction", value = "load-test-entity-extraction http://<rosetteIp>:<port>/rest/v1 <content> to be enriched")
    public void loadTestExportToFile(@ShellOption(defaultValue = "localhost:8181", help="Url where rosette standalone is installed") @Pattern(regexp = "https?:\\/\\/.+\\/rest\\/v1") String rosetteUrl,
                                           @ShellOption(help="Path to data source to send") String path,
                                           @ShellOption(help="Number of threads to work asynchronously to send data") String numberOfThreads,
                                           @ShellOption(help="Time to execute the test in minutes") String timeToExecute,
                                       @ShellOption(help="Url which gets machine statistics during entity extraction") String exportMachineStatisticsUrl,
                                       @ShellOption(help="If files need to be preloaded into memory") boolean shouldFilesBeLoadedIntoMemory
                                       ) throws IOException, ExecutionException, InterruptedException {

        File result = new File("resultDifferentRosetteConf");
        if (!result.exists()) {
            result.mkdir();
        }
        String[] languages = new String[] {"ALL"};
        List<StatisticsDTO> statisticInStringWithEEWithoutSentiment = new ArrayList<>();
        List<StatisticsDTO> statisticInStringWithoutEEWithSentiment = new ArrayList<>();
        List<StatisticsDTO> statisticInStringWithEEWithSentiment = new ArrayList<>();

        for (String language : languages) {
            StatisticsDTO withEEWithSentiment = commandService.getStatisticsDTO(rosetteUrl, path, numberOfThreads, timeToExecute, exportMachineStatisticsUrl, shouldFilesBeLoadedIntoMemory, true, true, (language.equals("ALL") ? new String[0] : new String[] {language}));
            statisticInStringWithEEWithSentiment.add(withEEWithSentiment);

//            writeToFile(String.format("%s_with_entity_extraction_%s.json", language, numberOfThreads), withEEWithoutSentiment, result);
//            writeToFile(String.format("%s_with_sentiment_%s.json", language, numberOfThreads), withoutEEWithSentiment, result);
            writeToFile(String.format("%s_with_entity_extraction_sentiment_%s.json", language, numberOfThreads), withEEWithSentiment, result);
        }
//        entitiesResponseCSVFormatter.format(statisticInStringWithEEWithoutSentiment, createFile(String.format("A_with_entity_extraction_%s.csv", numberOfThreads), result));
//        entitiesResponseCSVFormatter.format(statisticInStringWithoutEEWithSentiment, createFile(String.format("A_with_sentiment_%s.csv", numberOfThreads), result));
        entitiesResponseCSVFormatter.format(statisticInStringWithEEWithSentiment, createFile(String.format("A_with_entity_extraction_sentiment_%s.csv", numberOfThreads), result));
    //            writeToFile(String.format("%s_with_entity_extraction_%s.json", language, numberOfThreads), withEEWithoutSentiment, result);
        //            writeToFile(String.format("%s_with_sentiment_%s.json", language, numberOfThreads), withoutEEWithSentiment, result);
//        createFile(String.format("%s_with_entity_extraction_sentiment_%s.json", language, numberOfThreads), withEEWithSentiment, result);
    }
    @ShellMethod(key = "do-nlp-on-zips", value = "do-nlp-on-zips http://<rosetteIp>:<port>/rest/v1 <content> to be enriched")
    public String doNLPOnProbeZips(@ShellOption(defaultValue = "localhost:8181", help="Url where rosette standalone is installed") @Pattern(regexp = "https?:\\/\\/.+\\/rest\\/v1") String rosetteUrl,
                                          @ShellOption(help="Path to data source to send") String path,
                                          @ShellOption(help="Number of threads to work asynchronously to send data") String numberOfThreads,
                                          @ShellOption(help="Url which gets machine statistics during entity extraction") String exportMachineStatisticsUrl) throws IOException, ExecutionException, InterruptedException {
        LOGGER.info("Rosette on url:{} will be called for all files on path:{}", rosetteUrl, path);
        MachineLoadStatisticsDTO machineLoadStatisticsDTO = new MachineLoadStatisticsDTO();
        remoteMachineLoadMonitoringService.start(exportMachineStatisticsUrl, machineLoadStatisticsDTO);
        StatisticsDTO statisticsDTO = rosetteLoadTestService.enrichZipsComingFromDistributor(rosetteUrl, path, Integer.parseInt(numberOfThreads), false);
        remoteMachineLoadMonitoringService.stop();
        statisticsDTO.setMachineLoadStatisticsDTO(machineLoadStatisticsDTO);
        return entitiesResponseJsonFormatter.format(statisticsDTO);
    }

    public File createFile(String filename, File parent) throws IOException {
        File f = new File(parent, filename);
        if (f.exists()) {
            f.delete();
        }
        return f;

    }

    public void writeToFile(String filename, StatisticsDTO content, File parent) throws IOException {
        File f = new File(parent, filename);
        if (!f.exists()) {
            f.createNewFile();
        }
        FileUtils.write(f, entitiesResponseJsonFormatter.format(content), Charsets.UTF_8, false);

    }

}
