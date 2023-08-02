package com.webint.loadtest.iterator;

import com.webint.loadtest.dto.ProbeZipContent;
import com.webint.loadtest.service.ZipService;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProbeZipIterator implements Iterator<ProbeZipContent> {

    String[] TAGS = new String[]{"title", "text", "body"};
    public static final String REGEX = "<%s>([^<>]*)<\\/%s>";

    List<ProbeZipContent> zipContents = new ArrayList<>();
    Iterator<ProbeZipContent> zipContentIterator;

    public ProbeZipIterator(List<File> resources) throws IOException {

       for(File resource : resources) {
           List<String> arrayOfFiles = new ArrayList<>();
           ZipService.extractFolder(resource.getAbsolutePath(), arrayOfFiles);
           List<ProbeZipContent> probeZipContents = arrayOfFiles.stream().filter(p -> p.endsWith("xml")).map(path -> {
               try {
                   List<String> titles = getZip(path, TAGS[0]);
                   List<String> text = getZip(path, TAGS[1]);
                   List<String> body = getZip(path, TAGS[2]);
                   ProbeZipContent probeZipContent = new ProbeZipContent();
                   probeZipContent.setFilename(path);
                   probeZipContent.setText(text);
                   probeZipContent.setTitle(titles);
                   probeZipContent.setBody(body);
                   return probeZipContent;
               } catch (IOException e) {
                   throw new RuntimeException(e);
               }
           }).collect(Collectors.toList());
           zipContents.addAll(probeZipContents);
       }
    }

    public List<String> getZip(String pathToEntities, String tag) throws IOException {
        List<String> result = new ArrayList<>();
        File path = new File(pathToEntities);
        String fileContent = FileUtils.readFileToString(path, "UTF-8");
        Pattern pattern = Pattern.compile(String.format(REGEX, tag, tag));
        Matcher matcher = pattern.matcher(fileContent);
        while(matcher.find()) {
            String content = matcher.group(1);
            result.add(content);
        }
        return  result;



    }

    @Override
    public boolean hasNext() {
        if (zipContentIterator == null) {
            zipContentIterator = zipContents.iterator();
        }
        return zipContentIterator.hasNext();
    }

    @Override
    public ProbeZipContent next() {
        return zipContentIterator.next();
    }
}
