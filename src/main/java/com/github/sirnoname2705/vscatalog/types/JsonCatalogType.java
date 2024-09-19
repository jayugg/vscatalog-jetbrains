package com.github.sirnoname2705.vscatalog.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class JsonCatalogType {
    public String schema;
    public double version;
    public List<JsonCatalogEntryType> schemas;

    @JsonCreator
    public JsonCatalogType(
            @JsonProperty("$schema") String schema,
            @JsonProperty("version") double version,
            @JsonProperty("schemas") List<JsonCatalogEntryType> schemas) {
        this.schema = schema;
        this.version = version;
        this.schemas = schemas;
    }

    public JsonCatalogType(String content) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonCatalogType parsedCatalog = null;
        try {
            parsedCatalog = objectMapper.readValue(content,
                    JsonCatalogType.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        this.schema = parsedCatalog.schema;
        this.version = parsedCatalog.version;
        this.schemas = parsedCatalog.schemas;
    }
}

//public class JsonCatalogType {
//    public String schema;
//    public double version;
//    public List<JsonCatalogEntryType> schemas;
//
//    public JsonCatalogType(String content) {
//        Gson gson = new Gson();
//        JsonCatalogType parsedCatalog = gson.fromJson(content, JsonCatalogType.class);
//        this.schema = parsedCatalog.schema;
//        this.version = parsedCatalog.version;
//        this.schemas = parsedCatalog.schemas;
//    }
//
//    private JsonCatalogType(String schema, double version, List<JsonCatalogEntryType> schemas) {
//        this.schemas = schemas;
//        this.schema = schema;
//        this.version = version;
//    }
//
//    public static JsonCatalogType fromContent(String content) {
//
//    }
//
//
//}
