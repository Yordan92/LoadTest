package com.webint.loadtest.iterator;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class ResourceIterator implements Iterator<String[]> {

    Iterator<File> resourceIterator;
    List<File> resources;
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceIterator.class);

    public ResourceIterator(List<File> resources) {
        this.resources = resources;
        this.resourceIterator = resources.iterator();

    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public String[] next() {
        if (!resourceIterator.hasNext()) {
            resourceIterator = resources.iterator();
        }
        File resource = resourceIterator.next();
        try {
            String[] content = new String[]{resource.getAbsolutePath(), getFileContent(resource)};
            return content;
        } catch (IOException e) {
            LOGGER.error("Error during resource iteration", e);
        }
        return new String[2];

    }

    public String getFileContent(File resource) throws IOException {
        return FileUtils.readFileToString(resource, Charsets.UTF_8);
    }
}
