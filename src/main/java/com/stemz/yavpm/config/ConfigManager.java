package com.stemz.yavpm.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stemz.yavpm.YAVPM;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.lang.reflect.Field;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static YAVPMConfig CONFIG = new YAVPMConfig();

    private static File file() {
        return new File("config", YAVPM.MOD_ID + ".json");
    }

    public static void load() {
        try {
            if(!file().getParentFile().exists()) file().getParentFile().mkdirs();

            YAVPMConfig defaultConfig = new YAVPMConfig();
            if(file().exists()) {
                try (Reader r = new FileReader(file())) {
                    YAVPMConfig loadedConfig = GSON.fromJson(r, YAVPMConfig.class);
                    if(loadedConfig != null){
                        merge(defaultConfig, loadedConfig);
                    }
                }
            }

            CONFIG = defaultConfig;
            save();
            YAVPM.LOGGER.info("Loaded Config");
        } catch (Exception e) {
            YAVPM.LOGGER.error("Failed to load config", e);
        }
    }

    public static void save() {
        try (FileWriter w = new FileWriter(file())){
            GSON.toJson(CONFIG, w);
        }catch (Exception e) {
            YAVPM.LOGGER.error("Failed to save config", e);
        }
    }

    public static void reset() {
        CONFIG = new YAVPMConfig();
    }

    public static YAVPMConfig get() {
        return CONFIG;
    }

    private static void merge(Object defaults, Object custom) {
        for (Field f : defaults.getClass().getFields()){
            try {
                Object val = f.get(custom);
                if (val != null)
                    f.set(defaults, val);
            } catch (Exception ignored) {

            }
        }
    }
}
