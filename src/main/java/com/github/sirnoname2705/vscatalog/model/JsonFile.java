package com.github.sirnoname2705.vscatalog.model;

import com.github.sirnoname2705.vscatalog.DependencyResolver;
import com.github.sirnoname2705.vscatalog.PluginInitializer;
import com.github.sirnoname2705.vscatalog.remote.DownloadHelper;
import com.github.sirnoname2705.vscatalog.remote.MyUrl;
import com.github.sirnoname2705.vscatalog.types.UniqueStack;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Url;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.github.sirnoname2705.vscatalog.settings.Util.translateUrlToLocalPath;

public class JsonFile {

    public String url;
    protected boolean downloadStarted = false;
    private VirtualFile virtualFile;
    private String content;
    private String localPath;
    private boolean dirty = false;
    private boolean isFileDownloaded;
    private boolean dependenciesSatisfied;
    private List<JsonFile> children;

    public JsonFile(String url) {
        this.url = url;
        this.children = new ArrayList<>(5);
        if (!isPresentOnDisk()) {
            this.downloadJsonSync();
        }
    }

    public JsonFile(String url, boolean autoDownloadJson) {
        this.url = url;
        this.children = new ArrayList<>(5);
        if (autoDownloadJson) {
            this.downloadJsonSync();
        }
    }


    public void preLoadFile() {

    }

    public static JsonFile FromLocalPath(String url, Path localPath) {
        JsonFile jsonFile = new JsonFile(url, false);
        jsonFile.localPath = localPath.toString();
        jsonFile.isFileDownloaded = true;
        jsonFile.virtualFile = jsonFile.getLocalFileFromDisk();
        return jsonFile;
    }

    public VirtualFile getVirtualFile() {
        if (virtualFile != null) {
            return virtualFile;
        }
        if (isPresentOnDisk()) {
            VirtualFile localFile = getLocalFileFromDisk();
            this.virtualFile = localFile;
            this.isFileDownloaded = true;
            return virtualFile;
        } else {
//            LocalFileSystem.getInstance().refresh(false);
            this.downloadJsonSync();
            this.downloadStarted = true;
            this.virtualFile = getLocalFileFromDisk();
            this.isFileDownloaded = true;
            if (this.virtualFile == null) {
                System.err.println("Virtual file is null");
            }
            return virtualFile;
        }
    }

    public void downloadJson() {
        if (downloadStarted) {
            return;
        }
        this.ensureDirExists();
        PluginInitializer.contentProvider.downloadContent(getExternalUrl(), getLocalPath(), this::afterDownloadAction);
        downloadStarted = true;
    }

    public void downloadJsonSync() {
        if (downloadStarted) {
            return;
        }
        this.ensureDirExists();
        PluginInitializer.contentProvider.saveContentSync(getExternalUrl(), getLocalPath());
        this.afterDownloadAction();
        downloadStarted = true;
    }


    public void afterDownloadAction() {
        this.isFileDownloaded = true;
        this.virtualFile = this.getLocalFileFromDisk();
    }

    public Url getExternalUrl() {
        try {
            return new MyUrl(url);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getLocalPath() {
        if (localPath == null) {
            localPath = translateUrlToLocalPath(url);
        }
        return this.localPath;
    }

    public String getContent() {
        if (this.content == null || this.dirty) {
            try {
                var virtualFile = this.getVirtualFile();
                if (virtualFile != null) {
                    this.content = new String(virtualFile.contentsToByteArray());
                } else {
                    System.err.println("Virtual file is null");
                    return "";
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return this.content;
    }

    public boolean isPresentOnDisk() {
        var path = getLocalPath();
        return Files.exists(Path.of(path));
    }

    public void ensureDirExists() {
        Path schemaPath = Paths.get(getLocalPath());
        Path parentDir = schemaPath.getParent();
        DownloadHelper.ensureDirExists(parentDir);
    }


    private VirtualFile getLocalFileFromDisk() {
        var lfs = LocalFileSystem.getInstance();
//        lfs.refresh(true);
        return ApplicationManager.getApplication()
                .runReadAction((ThrowableComputable<VirtualFile, RuntimeException>) () -> {
                    return lfs.findFileByPath(this.getLocalPath());
                });
    }


    public List<JsonFile> getChildren() {
        return this.children;
    }

    public void findChildren() {
        if (this.children == null) {
            this.children = new ArrayList<>(5);
        }
        var content = this.getContent();
        assert content != null;
        var localPaths = DependencyResolver.getRefValues(content);
        for (String path : localPaths) {
            if (path.startsWith("#")) {
                continue;
            }
            var url = DependencyResolver.resolveUrl(this.url, path);
            if (url.isBlank()) {
                continue;
            }
            if (url.endsWith("#")) {
                url = url.replace("#", "");
            }
            JsonFile jsonFile = new JsonFile(url, false);
            this.addChildren(jsonFile);
        }
    }

    public void addChildren(JsonFile jsonFile) {
        this.children.add(jsonFile);
    }

    public boolean isAncestryReady() {
        if (!isPresentOnDisk()) {
            return false;
        }
        if (getVirtualFile() == null) {
            return false;
        }

        for (JsonFile child : getWholeAncestry().toArray()) {
            if (!child.isPresentOnDisk()) {
                return false;
            }
            if (child.getVirtualFile() == null) {
                return false;
            }
        }

        return true;
    }

    public UniqueStack<JsonFile> getWholeAncestry() {
        UniqueStack<JsonFile> ancestry = new UniqueStack<>();
        JsonFile current = this;
        for (JsonFile child : current.children) {
            ancestry.merge(child.getWholeAncestry());
        }

        return ancestry;
    }

    public boolean isReadyIterative() {
        UniqueStack<JsonFile> stack = new UniqueStack<>();
        stack.push(this);
        while (!stack.isEmpty()) {
            JsonFile current = stack.pop();
            if (!current.isPresentOnDisk()) {
                return false;
            }
            if (current.getVirtualFile() == null) {
                return false;
            }
            stack.pushAll(current.children);
        }
        return true;
    }

    public boolean isReady() {
        if (!isPresentOnDisk()) {
            return false;
        }
        if (getVirtualFile() == null) {
            return false;
        }
        for (JsonFile child : children) {
            if (!child.isReady()) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof JsonFile)) {
            return false;
        }
        JsonFile other = (JsonFile) obj;
        return this.url.equals(other.url);
    }
}
