package com.github.sirnoname2705.vscatalog.remote;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.impl.http.DefaultRemoteContentProvider;
import com.intellij.util.Url;
import com.intellij.util.io.HttpRequests;
import com.intellij.util.io.RequestBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class DownloadHelper extends DefaultRemoteContentProvider {
    private static final Logger LOG = Logger.getInstance(DownloadHelper.class);

    public static void ensureDirExists(Path path) {
        if (Files.notExists(path)) {
            try {
                if (!Files.isDirectory(path)) {
                    path = path.getParent();
                }
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void ensureDirExists(String path) {
        ensureDirExists(Path.of(path));
    }

    @Override
    public boolean canProvideContent(@NotNull Url url) {
        return super.canProvideContent(url);
    }

    public void downloadContent(@NotNull Url url, String location, Runnable callAfterFinished) {
        ensureDirExists(location);
        CompletableFuture<Void> downloadTask = new CompletableFuture<>();

        GenericCallBack myCallback = new GenericCallBack(callAfterFinished) {
            @Override
            public void finished(@Nullable FileType fileType) {
                LOG.info("Download finished for: " + url);
                downloadTask.complete(null);
            }

            @Override
            public void errorOccurred(@NotNull String errorMessage, boolean cancelled) {
                LOG.error("Download failed for: " + url + " with error: " + errorMessage);
                downloadTask.completeExceptionally(new RuntimeException(errorMessage));
            }
        };

        saveContent(url, new File(location), myCallback);
    }

    public void saveContentSync(@NotNull Url url, String location) {
        downloadContentSync(url, new File(location));
    }

    private void downloadContentSync(@NotNull Url url, @NotNull File file) {
        try {
            HttpRequests.request(url.toExternalForm()).saveToFile(file, null);
        } catch (IOException e) {
            LOG.info(e);
        }
    }

    @Override
    protected <T> T connect(@NotNull Url url, @NotNull RequestBuilder requestBuilder, HttpRequests.@NotNull RequestProcessor<T> processor) throws IOException {
        return super.connect(url, requestBuilder, processor);
    }

    @Override
    protected int getDefaultConnectionTimeout() {
        return super.getDefaultConnectionTimeout();
    }

    @Override
    protected @Nullable FileType adjustFileType(@Nullable FileType type, @NotNull Url url) {
        return super.adjustFileType(type, url);
    }

    @Override
    public boolean isUpToDate(@NotNull Url url, @NotNull VirtualFile local) {
        return super.isUpToDate(url, local);
    }

}
