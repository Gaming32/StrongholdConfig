package com.jemnetworks.strongholdconfig;

import java.util.logging.Logger;

import com.jemnetworks.strongholdconfig.util.CallableThreadGroup;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class StrongholdConfigPlugin extends JavaPlugin {
    public Logger logger;
    public FileConfiguration config;
    public ConfigurationSection strongholdConfigs;

    public StrongholdConfigWrapper originalDefaultConfig;
    public StrongholdConfigWrapper defaultConfig;

    @Override
    public void onEnable() {
        logger = getLogger();
        saveDefaultConfig();
        config = getConfig();
        strongholdConfigs = config.getConfigurationSection("configs");

        try {
            StrongholdModifier.failIfUnloaded();
            StrongholdPositionGenerator.failIfUnloaded();
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getCause() instanceof ExceptionInInitializerError) {
                Throwable e2 = e.getCause();
                if (e2.getCause() instanceof RuntimeException) {
                    e2 = e2.getCause();
                    if (e2.getCause() instanceof IllegalAccessException) {
                        logger.severe("Please make sure that you added the \"--add-opens=java.base/java.lang.reflect=ALL-UNNAMED\" JVM argument.");
                    }
                }
            }
            logger.severe("Initialization failed, disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        StrongholdModifier.threadGroup = new CallableThreadGroup(new ThreadGroup("Stronghold Generation Thread"));
        StrongholdModifier.threadGroup.setNameFormat("Stronghold Generation Thread - #%1$d");

        originalDefaultConfig = defaultConfig = StrongholdModifier.getDefaultConfig();

        ConfigurationSection defaultConfigSection = strongholdConfigs.getConfigurationSection("default");
        if (defaultConfigSection != null) {
            try {
                defaultConfig = new StrongholdConfigWrapper(
                    defaultConfigSection.getInt("distance", 32),
                    defaultConfigSection.getInt("spread", 3),
                    defaultConfigSection.getInt("count", 128)
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        getServer().getPluginManager().registerEvents(new StrongholdModifierListener(this), this);
        logger.info("StrongholdConfig enabled!");
    }

    public StrongholdConfigWrapper getWorldConfig(String name) throws ReflectiveOperationException {
        ConfigurationSection configSection = strongholdConfigs.getConfigurationSection(name);
        if (configSection == null) {
            return defaultConfig;
        }
        return new StrongholdConfigWrapper(
            configSection.getInt("distance", defaultConfig.getDistance()),
            configSection.getInt("spread", defaultConfig.getSpread()),
            configSection.getInt("count", defaultConfig.getCount())
        );
    }

    @Override
    public void onDisable() {
        logger.info("StrongholdConfig disabled");
    }
}
