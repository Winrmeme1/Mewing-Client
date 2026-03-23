/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.gui.navigation.windows;

import java.io.FileNotFoundException;
import java.io.IOException;

import net.mewing.gui.GridDefinition;
import net.mewing.gui.GridDefinition.RelativeUnit;
import net.mewing.gui.Thickness;
import net.mewing.gui.VerticalAlignment;
import net.mewing.gui.TextAlign;
import net.mewing.gui.components.ButtonComponent;
import net.mewing.gui.components.GridComponent;
import net.mewing.gui.components.ListComponent;
import net.mewing.gui.components.SeparatorComponent;
import net.mewing.gui.components.StackPanelComponent;
import net.mewing.gui.components.StringComponent;
import net.mewing.gui.components.TextBoxComponent;
import net.mewing.gui.navigation.Window;
import net.mewing.managers.SettingManager;

/**
 * Represents the Settings window that contains a list of all of the available
 * toggle-able settings.
 */
public class SettingsWindow extends Window {
	private final TextBoxComponent fileName;
	private final ListComponent configNames;

	public SettingsWindow() {
		super("Settings", 50, 770);

		StackPanelComponent stackPanel = new StackPanelComponent();
		stackPanel.setSpacing(4f);		

		// Title Bar
		stackPanel.addChild(new StringComponent("Settings Manager"));
		stackPanel.addChild(new SeparatorComponent());

		stackPanel.addChild(new StringComponent("Profile:", false));

		GridComponent loadConfigGrid = new GridComponent();
		loadConfigGrid.addColumnDefinition(new GridDefinition(1, RelativeUnit.Relative));
		loadConfigGrid.addColumnDefinition(new GridDefinition(100, RelativeUnit.Absolute));

		configNames = new ListComponent(SettingManager.configNames);
		loadConfigGrid.addChild(configNames);

		// Load Config Button
		ButtonComponent btnLoadConfig = new ButtonComponent(new Runnable() {
			@Override
			public void run() {
				SettingManager.setCurrentConfig(configNames.getSelectedItem());
			}
		});

		StringComponent buttonLoadString = new StringComponent("Load");
		buttonLoadString.setTextAlign(TextAlign.Center);
		buttonLoadString.setVerticalAlignment(VerticalAlignment.Center);
		btnLoadConfig.addChild(buttonLoadString);

		loadConfigGrid.addChild(btnLoadConfig);
		stackPanel.addChild(loadConfigGrid);

		// Grid containing config name textbox and save button.
		GridComponent fileSaveGrid = new GridComponent();
		fileSaveGrid.setHorizontalSpacing(4f);
		fileSaveGrid.setVerticalSpacing(4f);
		fileSaveGrid.addColumnDefinition(new GridDefinition(1, RelativeUnit.Relative));
		fileSaveGrid.addColumnDefinition(new GridDefinition(100, RelativeUnit.Absolute));

		// Config Name TextBox
		fileName = new TextBoxComponent();
		fileSaveGrid.addChild(fileName);

		// Save Button
		ButtonComponent btnSaveACopy = new ButtonComponent(new Runnable() {
			@Override
			public void run() {
				String newFileName = fileName.getText();
				if (!newFileName.isBlank()) {
					try {
						SettingManager.saveCopy(newFileName);
						SettingManager.refreshSettingFiles();
						configNames.setItemsSource(SettingManager.configNames);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						fileName.setText("");
					}
				}
			}
		});

		// Text inside of save button.
		StringComponent buttonStr = new StringComponent("Save");
		buttonStr.setTextAlign(TextAlign.Center);
		buttonStr.setVerticalAlignment(VerticalAlignment.Center);
		btnSaveACopy.addChild(buttonStr);
		fileSaveGrid.addChild(btnSaveACopy);
		stackPanel.addChild(fileSaveGrid);
		addChild(stackPanel);

		setMinWidth(300.0f);
	}
}
