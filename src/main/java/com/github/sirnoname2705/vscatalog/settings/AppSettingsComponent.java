package com.github.sirnoname2705.vscatalog.settings;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent {

    private final JPanel myMainPanel;
    private final JComboBox<String> versionComboBox;
    private final JBCheckBox autoUpdate = new JBCheckBox("Auto update on startup?");
    private final JButton checkForUpdatesButton = new JButton("Force Update");

    public AppSettingsComponent() {
        // Define the versions for the combo box
        String[] versions = {"current"};
        versionComboBox = new ComboBox<String>(versions);
        checkForUpdatesButton.addActionListener(e -> com.github.sirnoname2705.vscatalog.Updater.updateNow());

        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Select Version: "), versionComboBox, 1, false)
                .addComponent(autoUpdate, 1)
                .addComponent(this.checkForUpdatesButton)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return versionComboBox;
    }

    @NotNull
    public String getSelectedVersion() {
        return (String) versionComboBox.getSelectedItem();
    }

    public void setSelectedVersion(@NotNull String version) {
        versionComboBox.setSelectedItem(version);
    }

    public boolean getAutoUpdateStatus() {
        return autoUpdate.isSelected();
    }

    public void setAutoUpdateStatus(boolean newStatus) {
        autoUpdate.setSelected(newStatus);
        if (newStatus) {
            versionComboBox.setSelectedItem("current");
            versionComboBox.setEnabled(false);
        } else {
            versionComboBox.setEnabled(true);
        }
        myMainPanel.updateUI();
    }
}
