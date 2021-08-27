package com.jemnetworks.strongholdconfig;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class StrongholdModifierListener implements Listener {
    public boolean serverStarted = false;
    protected final StrongholdConfigPlugin plugin;
    protected Map<World, Thread> generationThreads = new HashMap<>();

    public StrongholdModifierListener(StrongholdConfigPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent e) {
        boolean skipped = false;
        StrongholdConfigWrapper newConfig = null;
        try {
            newConfig = plugin.getWorldConfig(e.getWorld().getName());
            Thread thread = StrongholdModifier.inject(e.getWorld(), newConfig, newConfig != plugin.defaultConfig, plugin.logger);
            skipped = thread == null;
            if (serverStarted && thread != null) {
                generationThreads.put(e.getWorld(), thread);
            }
        } catch (ReflectiveOperationException e1) {
            e1.printStackTrace();
            skipped = true;
        }
        if (skipped) {
            plugin.logger.info("Stronghold generation for level \"" + e.getWorld().getName() + "\" skipped.");
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        if (!serverStarted) return;
        Thread thread = generationThreads.remove(e.getWorld());
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent e) {
        if (e.getType() != ServerLoadEvent.LoadType.STARTUP) return;
        serverStarted = true;
        try {
            StrongholdModifier.threadGroup.join();
            StrongholdModifier.threadGroup.clear();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }
}
