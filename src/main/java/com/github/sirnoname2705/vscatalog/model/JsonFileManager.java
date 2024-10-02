package com.github.sirnoname2705.vscatalog.model;

import java.util.Map;
import java.util.Stack;

public class JsonFileManager {
    Map<String, JsonFile> jsonFiles;
    private static JsonFileManager instance;

    private JsonFileManager() {
        this.jsonFiles = new java.util.HashMap<>();
    }

    public static JsonFileManager getInstance() {
        if (instance == null) {
            instance = new JsonFileManager();
        }
        return instance;
    }

    public void clearCache() {
        jsonFiles.clear();
    }

    public void fillFromCatalog(JsonCatalog catalog) {
        Stack<JsonFile> filesToProcess = new Stack<>();
        filesToProcess.push(catalog);
        while (!filesToProcess.isEmpty()) {
            JsonFile file = filesToProcess.pop();
            if (doesFileExist(file.url)) {
                continue;
            }
            addFile(file);
            filesToProcess.addAll(file.getChildren());
        }
    }

    public boolean doesFileExist(String url) {
        // First Layer Cache
        if (jsonFiles.containsKey(url)) {
            return true;
        }
//        // Second Layer is the disk
//        var localPath = Path.of(translateUrlToLocalPath(url));
//        if (localPath.toFile().exists()) {
//            var loadedFile = JsonFile.FromLocalPath(url, localPath);
//            addFile(loadedFile);
//            return true;
//        }
        return false;
    }

    public void addFile(JsonFile file) {
        if (!file.isPresentOnDisk()) {
            file.downloadJsonSync();
        }
        assert file.getContent() != null;
        file.findChildren();
        jsonFiles.put(file.url, file);
    }

    public JsonFile getJsonFile(String url) {
        // First Layer Cache
        if (jsonFiles.containsKey(url)) {
            return jsonFiles.get(url);
        }
        // Second Layer is the disk
        //translateUrlToLocalPath(url);
//        JsonFile file = new JsonFile(url);
//        jsonFiles.put(url, file);
//
//        // Third Layer is the network
        return null;
    }
}
