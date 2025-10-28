package com.stemz.yavpm.integrations;

import com.stemz.yavpm.config.ConfigManager;
import com.stemz.yavpm.screens.config.ConfigMenuScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuEntryPoint implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new ConfigMenuScreen("YAVPM", parent);
    }
}
