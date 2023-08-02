package com.webint.loadtest.dto;

import com.basistech.rosette.dm.ArabicMorphoAnalysis;

import java.util.ArrayList;
import java.util.List;

public class ProbeZipContent {
    String filename;
    List<String> title;
    List<String> text;

    List<String> body;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<String> getTitle() {
        return title;
    }

    public void setTitle(List<String> title) {
        this.title = title;
    }

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }

    public List<String> getBody() {
        return body;
    }

    public void setBody(List<String> body) {
        this.body = body;
    }

    public List<String> getAllEntities() {
        List<String> allElements = new ArrayList<>();
        allElements.addAll(text);
        allElements.addAll(title);
        allElements.addAll(body);
        return allElements;
    }
}
