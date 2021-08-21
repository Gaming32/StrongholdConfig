package com.jemnetworks.strongholdconfig;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class StrongholdConfigPlugin extends JavaPlugin {
    public Logger logger;

    @Override
    public void onEnable() {
        logger = getLogger();
        getServer().getPluginManager().registerEvents(new StronholdModifierListener(this), this);
        logger.info("StrongholdConfig enabled!");
    }
}
