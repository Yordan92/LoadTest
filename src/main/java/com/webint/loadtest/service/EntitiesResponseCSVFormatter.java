package com.webint.loadtest.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webint.loadtest.dto.ResourceStatistic;
import com.webint.loadtest.dto.StatisticsDTO;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EntitiesResponseCSVFormatter  {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void format(List<StatisticsDTO> entitiesResponse, File csv) throws JsonProcessingException {
        String[] col = new String[] {"Language", "req/min", "words/sec", "success", "CPU", "RAM"};
        Map<String, List<StatisticsDTO>> groupedByLan = new TreeMap<>();
        for(StatisticsDTO statisticsDTO : entitiesResponse) {
            String key = statisticsDTO.getTestExecutedForLanguages().isEmpty() ? "ALL" : statisticsDTO.getTestExecutedForLanguages().iterator().next();
            groupedByLan.compute(key, (k,v)-> v == null ? new ArrayList<>() : v).add(statisticsDTO);
        }
        List<List<String>> results = new ArrayList<>();
        results.add(Arrays.asList(col));
        for (String lang : groupedByLan.keySet()) {
            List<StatisticsDTO> statisticsDTOSPerLang = groupedByLan.get(lang);
            for (StatisticsDTO statisticsDTO :  statisticsDTOSPerLang) {
                long reqPerMin = statisticsDTO.getNumberOfRequests();
                double wordsPerSecond = statisticsDTO.getWordsPerSecond();
                double successRate;
                long numberOfFailures = 0;
                long numberOfSuccesses = 0;
                for (ResourceStatistic resourceStatistic : statisticsDTO.getResourceStatisticMap().values()) {
                    numberOfSuccesses += resourceStatistic.getTimesSendForEnrichment();
                    numberOfFailures += resourceStatistic.getNumberOfFailures();
                }
                successRate = numberOfSuccesses * 1.0 / (numberOfSuccesses + numberOfFailures);
                String[] row = new String[] {lang, Long.toString(reqPerMin), Double.toString(wordsPerSecond), Double.toString(successRate*100), String.valueOf(statisticsDTO.getMachineLoadStatisticsDTO().getCpuLoad().getAvg()), String.valueOf(statisticsDTO.getMachineLoadStatisticsDTO().getRam().getAvg()/1000)};
                results.add(Arrays.asList(row));
            }
        }
        storeCSV(results, csv);
    }


    public static void storeCSV(List<List<String>> context, File csv) {
        List<String> fileLines = new ArrayList<>();

        if (!csv.exists()) {
            try {
                csv.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (List<String> row : context) {
            String rowAsCSV = row.stream().map(column -> {
                if (column == null) {
                    return "-";
                }
                return column.replaceAll(",", " ");
            }).collect(Collectors.joining(","));
            fileLines.add(rowAsCSV);
        }
        try {
            FileUtils.writeLines(csv, fileLines, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
