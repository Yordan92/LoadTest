package com.webint.loadtest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webint.loadtest.dto.MachineLoadStatisticsDTO;
import com.webint.loadtest.dto.StatisticsDTO;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@Service
public class RemoteMachineLoadMonitoringService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteMachineLoadMonitoringService.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private boolean CHECK_STATISTICS = true;

    private long lastMeasuredCPUTime = 0;
    private long timeWhenMeasured = 0;
    private long lastTimeSystemResourcesLogged = 0;
    Future calculateStatistics;
    ExecutorService threadSomething;

    public void start(String url, MachineLoadStatisticsDTO statisticsDTO) {
        CHECK_STATISTICS = true;
        threadSomething = Executors.newFixedThreadPool(1);
        calculateStatistics = threadSomething.submit(() -> {
//            try {
            while (CHECK_STATISTICS) {
                callCadvisor(url + "/api/v1.3/containers/", statisticsDTO);
                if (System.currentTimeMillis() - lastTimeSystemResourcesLogged > 10 * 1000) {
                    LOGGER.info("Load for cpu in %: {} ", statisticsDTO.getCpuLoad());
                    LOGGER.info("Load for ram in MBs: {}", statisticsDTO.getRam());
                    lastTimeSystemResourcesLogged = System.currentTimeMillis();

                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LOGGER.error("", e);
                }
            }
//            } catch (InterruptedException  e) {
//                LOGGER.error("Problem getting data from queue", e);
//            }
        });
    }

    private void callCadvisor(String url, MachineLoadStatisticsDTO statisticsDTO) {
        try {
            URL cadvisorURL = new URL(url );
            HttpURLConnection connection = (HttpURLConnection) cadvisorURL.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.getResponseCode();
            InputStream inputStream = connection.getInputStream();
            String content = IOUtils.toString(inputStream);
            Map<String, Object> contentMap = OBJECT_MAPPER.readValue(content, Map.class);
            if (contentMap.containsKey("stats")) {
                List<Map> stats = (List) contentMap.get("stats");
                Map lastStat = stats.size() > 1 ? stats.get(stats.size() - 1) : Collections.emptyMap();
                Map previousStat = stats.size() > 0 ? stats.get(stats.size() - 2) : Collections.emptyMap();

                Instant prev = getTimeFromStat(previousStat);
                Instant last = getTimeFromStat(lastStat);
                Duration timeBetween = Duration.between(prev, last);
                long time = timeBetween.getSeconds() * (long) Math.pow(10, 9) + timeBetween.getNano();
                long cpuTime = getCPUUsage(lastStat) - getCPUUsage(previousStat);
                double cpuUsageInPresent = 1.0 * cpuTime / time * 100;

                statisticsDTO.addCpuLoad(cpuUsageInPresent);
                statisticsDTO.addRam(getRamUsage(lastStat));
            }



            connection.disconnect();
        } catch(Exception e) {
            LOGGER.error("", e);
        }
    }

    private Instant getTimeFromStat(Map stat) throws ParseException {
        String date = (String) stat.get("timestamp");
        date = date.replace("Z","");
        LocalDateTime localDateTime = LocalDateTime.parse(date);
        return ZonedDateTime.of(localDateTime, ZoneOffset.UTC).toInstant();
    }
    private long getCPUUsage(Map stat){
        if (stat.containsKey("cpu")) {
            Map cpu = (Map) stat.get("cpu");
            Map usage = (Map) cpu.get("usage");
            long total = (long) usage.get("total");
            return total;
        }
        return 0;
    }

    private long getRamUsage(Map stat) {
        if (stat.containsKey("memory")) {
            Map usage = (Map) stat.get("memory");
            long total = (long) usage.get("working_set");
            return total / 1024 / 1024;
        }
        return 0;
    }
    public void stop() {
        CHECK_STATISTICS = false;
        calculateStatistics.cancel(true);
        threadSomething.shutdown();
    }
}
