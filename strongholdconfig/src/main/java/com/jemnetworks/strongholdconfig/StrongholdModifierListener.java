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
        boolean skipped = false;
        StrongholdConfigWrapper newConfig = null;
        try {
            newConfig = plugin.getWorldConfig(e.getWorld().getName());
            skipped = StrongholdModifier.inject(e.getWorld(), newConfig, newConfig != plugin.defaultConfig, plugin.logger) == -1;
        } catch (ReflectiveOperationException e1) {
            e1.printStackTrace();
            skipped = true;
        }
        if (skipped) {
            plugin.logger.info("Stronghold generation for level \"" + e.getWorld().getName() + "\" skipped.");
        }
    }
}
