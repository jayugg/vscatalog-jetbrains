package com.github.sirnoname2705.vscatalog.remote;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.impl.http.RemoteContentProvider;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GenericCallBack implements RemoteContentProvider.DownloadingCallback {

    private final Runnable onFinishedCallback;

    public GenericCallBack(Runnable function) {
        this.onFinishedCallback = function;
    }

    public GenericCallBack() {
        this(null);
    }

    @Override
    public void finished(@Nullable FileType fileType) {
        if (onFinishedCallback != null) {
            onFinishedCallback.run();  // Execute the callback function
        }
    }

    @Override
    public void errorOccurred(@NotNull String errorMessage, boolean cancelled) {

    }

    @Override
    public void setProgressText(@NotNull String text, boolean indeterminate) {

    }

    @Override
    public void setProgressFraction(double fraction) {

    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}
