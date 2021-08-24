package com.jemnetworks.strongholdconfig;

import java.util.logging.Logger;

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
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

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
