package com.jemnetworks.strongholdconfig;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

public class StrongholdModifierListener implements Listener {
    final StrongholdConfigPlugin plugin;

    public StrongholdModifierListener(StrongholdConfigPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent e) {
        long time = -1;
        plugin.logger.info("Generating strongholds for world " + e.getWorld().getName());
        StrongholdConfigWrapper newConfig = null;
        try {
            newConfig = plugin.getWorldConfig(e.getWorld().getName());
            time = StrongholdModifier.inject(e.getWorld(), newConfig, newConfig == plugin.defaultConfig); // If the section is defined in the config, but is empty, the configs will be .equals() but not ==
        } catch (ReflectiveOperationException e1) {
            e1.printStackTrace();
            time = -1;
        }
        if (time == -1) {
            plugin.logger.info("Stronghold generation for world " + e.getWorld().getName() + " skipped.");
        } else {
            plugin.logger.info("Successfully generated " + newConfig.getCount() + " strongholds for world " + e.getWorld().getName() + " in " + time / 1000d + " seconds.");
        }
    }
}
