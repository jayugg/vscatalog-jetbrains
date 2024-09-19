package com.github.sirnoname2705.vscatalog.model;

import com.github.sirnoname2705.vscatalog.types.JsonCatalogEntryType;
import com.github.sirnoname2705.vscatalog.types.JsonCatalogType;
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider;

import java.util.ArrayList;
import java.util.List;


public class JsonCatalog extends JsonFile {
    private JsonCatalogType catalogType;
    private List<SchemaFile> schemaFiles;

    public JsonCatalog(String url) {
        super(url);
    }

    public List<JsonSchemaFileProvider> GetFileProviders() {
        var providers = new ArrayList<JsonSchemaFileProvider>(50);
        providers.addAll(this.schemaFiles);
        return providers;
    }

    public void postInit() {
        this.catalogType = new JsonCatalogType(getContent());
        schemaFiles = new ArrayList<>();
        for (JsonCatalogEntryType entry : this.catalogType.schemas) {
            SchemaFile file = entry.toSchemaFile();
            this.addChildren(file);
            this.schemaFiles.add(file);
        }
    }


    public JsonCatalogType getParsedCatalog() {
        return catalogType;
    }
}
