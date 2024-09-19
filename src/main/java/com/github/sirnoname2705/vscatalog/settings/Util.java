package com.github.sirnoname2705.vscatalog.settings;

public class Util {

    public static final String DEFAULT_URL_PROTOCOL = "https";
    public static final String DEFAULT_URL_AUTHORITY = "raw.githubusercontent.com";
    public static final String DEFAULT_URL_BASE_PATH = "/SirNoName2705/VintelliSchemas/master/SchemaReleases/";
    public static final String DEFAULT_URL_VERSION = "current";
    public static final String URL_BASE = String.format("%s://%s%s",
            DEFAULT_URL_PROTOCOL,
            DEFAULT_URL_AUTHORITY,
            DEFAULT_URL_BASE_PATH
    );
    public static final String CATALOG_FILE_NAME = "vs_schema_catalog.json";

    public static final String CATALOG_URL = URL_BASE + "/" + CATALOG_FILE_NAME;
    public static final String NEWEST_CATALOG =
            "https://raw.githubusercontent.com/SirNoName2705/VintelliSchemas/master/SchemaReleases/current/" +
                    CATALOG_FILE_NAME;
    public static final String LOCAL_BASE =
            com.intellij.openapi.application.PathManager.getConfigPath() + "/vscatalog/";
    public static final String SCHEMA_VERSION_PATTERN_STRING = "\"\\{\\s*\\\"\\$schema\\\":\\s*\\\"(.*)\\\"\"";
    public static final String refPattern = ".*\\\"\\$ref\\\".*\\\"(.*)\\\"";
    public static String SOURCE_URL;
    public static String LOCAL_SOURCE;

    public static String translateUrlToLocalPath(String url) {
        return url.replace(getSourceUrl(), getLocalUrl());
    }

    public static String getLocalUrl() {
        if (LOCAL_SOURCE == null) {
            LOCAL_SOURCE = LOCAL_BASE + AppSettings.getInstance().getSourceVersion();
        }
        return LOCAL_SOURCE;
    }

    public static String getCatalogUrl() {
        return getSourceUrl() + "/" + CATALOG_FILE_NAME;
    }

    public static String getSourceUrl() {
        if (SOURCE_URL == null) {
            SOURCE_URL = URL_BASE + AppSettings.getInstance().getSourceVersion();
        }
        return SOURCE_URL;
    }

    public static boolean isAutoUpdate() {
        return com.github.sirnoname2705.vscatalog.settings.AppSettings.getInstance().getState().autoUpdateOnStartup;
    }

}
