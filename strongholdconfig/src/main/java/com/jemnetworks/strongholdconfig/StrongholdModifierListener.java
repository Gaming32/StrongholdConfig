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
        boolean necessary;
        long time = -1;
        plugin.logger.info("Generating strongholds for world " + e.getWorld().getName());
        try {
            StrongholdConfigWrapper newConfig = plugin.getWorldConfig(e.getWorld().getName());
            // necessary = !newConfig.equals(plugin.defaultConfig);
            necessary = true;
            if (necessary) {
                time = StrongholdModifier.inject(e.getWorld(), newConfig);
            }
        } catch (ReflectiveOperationException e1) {
            e1.printStackTrace();
            return;
        }
        necessary = necessary && time != -1;
        if (necessary) {
            plugin.logger.info("Successfully generated strongholds for world " + e.getWorld().getName() + " in " + time / 1000d + " seconds.");
        } else {
            plugin.logger.info("Stronghold generation for world " + e.getWorld().getName() + " skipped.");
        }
    }
}
