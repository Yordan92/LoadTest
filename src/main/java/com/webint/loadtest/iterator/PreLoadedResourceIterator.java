package com.webint.loadtest.iterator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreLoadedResourceIterator extends ResourceIterator {

    Map<String, String> resourceContentMap;
    public PreLoadedResourceIterator(List<File> resources) throws IOException {
        super(resources);
        resourceContentMap = new HashMap<>();
        for (File f : resources) {
            resourceContentMap.put(f.getAbsolutePath(), super.getFileContent(f));
        }
    }

    @Override
    public String getFileContent(File f) {
        return resourceContentMap.get(f.getAbsolutePath());
    }
}
