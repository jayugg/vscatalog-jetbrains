package com.github.sirnoname2705.vscatalog.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.sirnoname2705.vscatalog.model.SchemaFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonCatalogEntryType {
    public String name;
    public String description;
    public final List<String> fileMatch = new ArrayList<>();
    public String url;


//    public JsonCatalogEntryType(String name, String description, String url, String[] fileMatch) {
//        if (url == null) {
//            throw new IllegalArgumentException("url must not be null");
//        }
//        if (description == null) {
//            description = "";
//        }
//        if (name == null) {
//            name = getNameFromUrl(url);
//        }
//
//        this.name = name;
//        this.description = description;
//        this.url = url;
//        this.fileMatch.addAll(Arrays.stream(fileMatch).toList());
//    }

    @JsonCreator
    public JsonCatalogEntryType(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("url") String url,
            @JsonProperty("fileMatch") String[] fileMatch) {
        if (url == null) {
            throw new IllegalArgumentException("url must not be null");
        }
        if (description == null) {
            description = "";
        }
        if (name == null) {
            name = getNameFromUrl(url);
        }

        this.name = name;
        this.description = description;
        this.url = url;
        this.fileMatch.addAll(Arrays.stream(fileMatch).toList());
    }

    private String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);

    }

    public void addFileMatch(String string) {
        fileMatch.add(string);
    }

    public SchemaFile toSchemaFile() {
        return new SchemaFile(url, name, description, fileMatch);
    }

    @Override
    public String toString() {
        return "JsonCatalogEntryType{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", fileMatches=" + fileMatch +
                ", url='" + url + '\'' +
                '}';
    }
}
