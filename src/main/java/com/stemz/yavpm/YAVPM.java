package com.stemz.yavpm;

import com.stemz.yavpm.config.ConfigManager;
import com.stemz.yavpm.features.RightClickHarvest;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YAVPM implements ModInitializer {

	public static final String MOD_ID = "yavpm";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        LOGGER.info("Initializing YAVPM");
        ConfigManager.load();

        RightClickHarvest.register();


        ModItems.init();
        ModBlocks.init();

        LOGGER.info("Initialized YAVPM");
	}
}