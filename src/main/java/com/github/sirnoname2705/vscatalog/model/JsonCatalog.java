package com.github.sirnoname2705.vscatalog.model;

import com.github.sirnoname2705.vscatalog.types.JsonCatalogEntryType;
import com.github.sirnoname2705.vscatalog.types.JsonCatalogType;
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.sirnoname2705.vscatalog.settings.Util.urlPattern;


public class JsonCatalog extends JsonFile {
    private JsonCatalogType catalogType;
    private List<SchemaFile> schemaFiles;
    private Pattern compiledPattern = Pattern.compile(urlPattern);

    public JsonCatalog(String url) {
        super(url);
    }

    public List<JsonSchemaFileProvider> GetFileProviders() {
        var providers = new ArrayList<JsonSchemaFileProvider>(50);
        providers.addAll(this.schemaFiles);
        return providers;
    }

    public List<JsonSchemaFileProvider> GetEmptyFileProviders() {
        this.catalogType = new JsonCatalogType(getContent());
        schemaFiles = new ArrayList<>();
        for (JsonCatalogEntryType entry : this.catalogType.schemas) {
            SchemaFile file = entry.toSchemaFile(false);
            this.schemaFiles.add(file);
        }
        return GetFileProviders();
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

    @Override
    public void findChildren() {
        var content = this.getContent();
        assert content != null;
        Matcher matcher = compiledPattern.matcher(content);
        while (matcher.find()) {
            var result = matcher.group(1);
            JsonFile jsonFile = new JsonFile(result, false);
            this.addChildren(jsonFile);
        }
    }
}
