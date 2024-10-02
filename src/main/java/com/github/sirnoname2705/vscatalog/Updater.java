package com.github.sirnoname2705.vscatalog;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static com.github.sirnoname2705.vscatalog.SchemaProviderFactory.resetSchemaService;
import static com.github.sirnoname2705.vscatalog.SchemaProviderFactory.restartPlugin;
import static com.github.sirnoname2705.vscatalog.settings.Util.getLocalUrl;

public class Updater {

    public static void updateIfAvailable() {
        if (isUpdateAvailable()) {
            updateNowV2();
        }

    }

    public static void forceUpdate() {
        updateNowV2();
    }


    public static void updateNow() {
        com.github.sirnoname2705.vscatalog.SchemaProviderFactory.IS_READY = false;
        com.github.sirnoname2705.vscatalog.SchemaProviderFactory.SHOULD_INIT = false;
        resetSchemaService();

        var currentFilePath = java.nio.file.Path.of(getLocalUrl());
        var newFilePath = java.nio.file.Path.of(currentFilePath.toString() + "_old");

        try {
            if (java.nio.file.Files.exists(newFilePath)) {
                deleteDirectoryRecursively(newFilePath);
            }
            java.nio.file.Files.createDirectories(newFilePath);

            java.nio.file.Files.walkFileTree(currentFilePath, new java.nio.file.SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir,
                                                         java.nio.file.attribute.BasicFileAttributes attrs) throws
                        java.io.IOException {
                    Path targetDir = newFilePath.resolve(currentFilePath.relativize(dir));
                    java.nio.file.Files.createDirectories(targetDir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, java.nio.file.attribute.BasicFileAttributes attrs) throws
                        java.io.IOException {
                    Path targetFile = newFilePath.resolve(currentFilePath.relativize(file));
                    java.nio.file.Files.move(file, targetFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        restartPlugin();
    }

    public static void updateNowV2() {
        com.github.sirnoname2705.vscatalog.SchemaProviderFactory.IS_READY = false;
        com.github.sirnoname2705.vscatalog.SchemaProviderFactory.SHOULD_INIT = false;
        resetSchemaService();
        var currentFilePath = java.nio.file.Path.of(getLocalUrl());
        try {
            Files.move(currentFilePath, Path.of(currentFilePath.toString() + "_old"),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (java.io.IOException e) {
            System.err.println("Error while renaming the current directory" + currentFilePath.toString());
            e.printStackTrace();
        }
        restartPlugin();
    }

    private static void deleteDirectoryRecursively(Path path) throws java.io.IOException {
        java.nio.file.Files.walkFileTree(path, new java.nio.file.SimpleFileVisitor<>() {
            @Override
            public java.nio.file.FileVisitResult visitFile(Path file,
                                                           java.nio.file.attribute.BasicFileAttributes attrs) throws
                    java.io.IOException {
                java.nio.file.Files.delete(file);
                return java.nio.file.FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, java.io.IOException exc) throws java.io.IOException {
                java.nio.file.Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }


    public static boolean isUpdateAvailable() {
        var currentCatalog = PluginInitializer.getCatalogIfReady();
        if (currentCatalog == null) {
            return false;
        }
        String newerCatalog = JsonFetcher.fetchJsonFromUrl();
        var currentCatalogContent = currentCatalog.getContent();
        return !newerCatalog.equals(currentCatalogContent);
    }

}
