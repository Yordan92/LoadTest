package com.webint.loadtest.service;


import com.webint.loadtest.iterator.PreLoadedResourceIterator;
import com.webint.loadtest.iterator.ProbeZipIterator;
import com.webint.loadtest.iterator.ResourceIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class LoadTestResourceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadTestResourceService.class);

    public ResourceIterator loadResourcesForLanguages(String pathToResources, Set<String> fileLanguages, boolean shouldPreload) throws IOException {
        File resourcesFolder = new File(pathToResources);
        Map<String, List<File>> langToContent = new LinkedHashMap<>();
        if (resourcesFolder.isDirectory()) {
            LOGGER.info("Following path points to directory: {}, all files will be send for enrichment", resourcesFolder.getAbsolutePath());
            for (File file : resourcesFolder.listFiles()) {
                if (file.isFile()) {
                    mapLanguageToFile(langToContent, file);
                }
            }
        }
        List<File> resourcesForSpecificLanguages = getResourcesForLanguages(fileLanguages, langToContent);
        ResourceIterator resourceIterator;
        if (shouldPreload) {
            resourceIterator = new PreLoadedResourceIterator(resourcesForSpecificLanguages);
        } else {
            resourceIterator = new ResourceIterator(resourcesForSpecificLanguages);
        }
        return resourceIterator;
    }

    private List<File> getResourcesForLanguages(Set<String> fileLanguages, Map<String, List<File>> langToContent) {
        List<File> resourcesForSpecificLanguages = new ArrayList<>();
        if (fileLanguages.isEmpty()) {
            langToContent.values().stream().flatMap(List::stream).forEach(resourcesForSpecificLanguages::add);
        } else  {
            for (String key : langToContent.keySet()) {
                if (fileLanguages.contains(key)) {
                    resourcesForSpecificLanguages.addAll(langToContent.get(key));
                }
            }
        }
        return resourcesForSpecificLanguages;
    }

    private static void mapLanguageToFile(Map<String, List<File>> langToContent, File file) throws IOException {
        String fileName = file.getName();
        String language = fileName.split("_")[0];
        if (!langToContent.containsKey(language)) {
            List<File> list = new ArrayList<>();
            list.add(file);
            langToContent.put(language, list);
        } else {
            langToContent.get(language).add(file);
        }
    }

    public ProbeZipIterator loadProbeZipsForEnrichment(String pathToResources) throws IOException {
        File resourcesFolder = new File(pathToResources);
        List<File> zips = new ArrayList<>();
        if (resourcesFolder.isDirectory()) {
            LOGGER.info("Following path points to directory: {}, all files will be send for enrichment", resourcesFolder.getAbsolutePath());
            for (File file : resourcesFolder.listFiles()) {
                if (file.isFile() && file.getName().contains("zip")) {
                    zips.add(file);
                }
            }
            ProbeZipIterator probeZipIterator = new ProbeZipIterator(zips);
            return probeZipIterator;
        }
        return null;
    }
}
