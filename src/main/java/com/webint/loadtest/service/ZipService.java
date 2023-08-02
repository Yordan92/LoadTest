package com.webint.loadtest.service;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class ZipService {

    private static final String XML = ".xml";
    private static final String ZIP = ".zip";
    private static final Logger LOGGER = LoggerFactory.getLogger(ZipService.class);

    static public List<String> extractFolder(String zipFile, List<String> listOfFileNames)
            throws ZipException, IOException {
        System.out.println(zipFile);
        int BUFFER = 2048;
        File file = new File(zipFile);

        @SuppressWarnings("resource")
        ZipFile zip = new ZipFile(file);
        String newPath = zipFile.substring(0, zipFile.length() - 4);

        new File(newPath).mkdir();
        Enumeration<?> zipFileEntries = zip.entries();

        // Process each entry
        while (zipFileEntries.hasMoreElements()) {
            // grab a zip file entry
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            String currentEntry = entry.getName();
            File destFile = new File(newPath, currentEntry);
            // destFile = new File(newPath, destFile.getName());
            File destinationParent = destFile.getParentFile();

            // create the parent directory structure if needed
            if (destinationParent.exists()) {
                FileUtils.deleteDirectory(destinationParent);
            }
            destinationParent.mkdirs();
            listOfFileNames.add(newPath.concat("\\").concat(currentEntry));

            if (!entry.isDirectory()) {
                BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
                int currentByte;
                // establish buffer for writing file
                byte data[] = new byte[BUFFER];

                // write the current file to disk
                FileOutputStream fos = new FileOutputStream(destFile);
                BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

                // read and write until last byte is encountered
                while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, currentByte);
                }
                dest.flush();
                dest.close();
                is.close();
            }

            if (currentEntry.endsWith(ZIP)) {
                // found a zip file, try to open
                extractFolder(destFile.getAbsolutePath(), listOfFileNames);
            }
        }
        zip.close();
        return listOfFileNames;
    }

    public void unzipFileIntoDirectory(File archive, File destinationDir) throws Exception {
        final int BUFFER_SIZE = 1024;
        BufferedOutputStream dest = null;
        FileInputStream fis = new FileInputStream(archive);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
        ZipEntry entry;
        File destFile;
        while ((entry = zis.getNextEntry()) != null) {
            destFile = new File(destinationDir, entry.getName());
            if (entry.isDirectory()) {
                destFile.mkdirs();
                continue;
            } else {
                int count;
                byte data[] = new byte[BUFFER_SIZE];
                destFile.getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream(destFile);
                dest = new BufferedOutputStream(fos, BUFFER_SIZE);
                while ((count = zis.read(data, 0, BUFFER_SIZE)) != -1) {
                    dest.write(data, 0, count);
                }
                dest.flush();
                dest.close();
                fos.close();
            }
        }
        zis.close();
        fis.close();
    }
}