package com.github.sirnoname2705.vscatalog;

import com.github.sirnoname2705.vscatalog.model.JsonCatalog;
import com.github.sirnoname2705.vscatalog.remote.DownloadHelper;
import com.intellij.openapi.vfs.impl.http.RemoteFileManager;

import static com.github.sirnoname2705.vscatalog.settings.Util.getCatalogUrl;
import static com.github.sirnoname2705.vscatalog.settings.Util.isAutoUpdate;

public class PluginInitializer {

    public static DownloadHelper contentProvider;
    public static RemoteFileManager rfm;
    public static JsonCatalog catalog;
    private static PluginInitializer _instance;

    private PluginInitializer() {
        contentProvider = new DownloadHelper();
        PluginInitializer.contentProvider = new DownloadHelper();
        PluginInitializer.rfm = RemoteFileManager.getInstance();
    }

    public void initializePlugin() {
        catalog = new JsonCatalog(getCatalogUrl());
        if (catalog.isPresentOnDisk()) {
            catalog.postInit();
        }
        if (catalog.isReady()) {
            SchemaProviderFactory.providers = catalog.GetFileProviders();
            SchemaProviderFactory.setJsonSchemasReady();
        }
        if (isAutoUpdate()) {
            Updater.updateIfAvailable();
        }
    }

    public static JsonCatalog getCatalogIfReady() {
        if (catalog != null && catalog.isReady()) {
            return catalog;
        }
        return null;
    }

    public static PluginInitializer getInstance() {
        if (PluginInitializer._instance == null) {
            _instance = new PluginInitializer();
        }
        return _instance;
    }


}
