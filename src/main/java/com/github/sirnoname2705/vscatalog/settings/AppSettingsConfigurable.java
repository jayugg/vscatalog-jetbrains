// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.github.sirnoname2705.vscatalog.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

/**
 * Provides controller functionality for application settings.
 */
final class AppSettingsConfigurable implements Configurable {

    private AppSettingsComponent mySettingsComponent;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "VScatalog";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new AppSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettings.State state =
                Objects.requireNonNull(AppSettings.getInstance().getState());
        if (!mySettingsComponent.getSelectedVersion().equals(state.version)) {
            return true;
        }
        if (!mySettingsComponent.getAutoUpdateStatus() == state.autoUpdateOnStartup) {
            return true;
        }
        return false;
    }

    @Override
    public void apply() {
        AppSettings.State state =
                Objects.requireNonNull(AppSettings.getInstance().getState());
        state.version = mySettingsComponent.getSelectedVersion();
        state.autoUpdateOnStartup = mySettingsComponent.getAutoUpdateStatus();
    }

    @Override
    public void reset() {
        AppSettings.State state =
                Objects.requireNonNull(AppSettings.getInstance().getState());
        mySettingsComponent.setSelectedVersion(state.version);
        mySettingsComponent.setAutoUpdateStatus(state.autoUpdateOnStartup);
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }

}
