package com.jemnetworks.strongholdconfig;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.jemnetworks.strongholdconfig.util.CallableThreadGroup;
import com.jemnetworks.strongholdconfig.util.FieldHelper;
import com.jemnetworks.strongholdconfig.util.TypedField;

import org.bukkit.World;

import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.configurations.StructureSettingsStronghold;

public class StrongholdModifier {
    public static CallableThreadGroup threadGroup = null;

    private static Throwable loadException = null;

    // private static Field STRONGHOLD_FIELD;
    private static TypedField<StructureSettingsStronghold> STRONGHOLD_FIELD;

    private static TypedField<List<ChunkCoordIntPair>> STRONGHOLDS_FIELD;

    static {
        try {
            STRONGHOLD_FIELD = TypedField.getDeclaredField(StructureSettings.class, "e");
            STRONGHOLD_FIELD.getWrapped().setAccessible(true);
            FieldHelper.makeNonFinal(STRONGHOLD_FIELD);

            STRONGHOLDS_FIELD = TypedField.getDeclaredField(ChunkGenerator.class, "f");
            STRONGHOLDS_FIELD.getWrapped().setAccessible(true);
        } catch (Throwable e) {
            loadException = e;
        }
    }

    public static StrongholdConfigWrapper getDefaultConfig() {
        return new StrongholdConfigWrapper(StructureSettings.c);
    }

    private static ChunkGenerator getGeneratorFromWorld(World world) throws ReflectiveOperationException {
        WorldServer worldServer = (WorldServer)world.getClass().getDeclaredMethod("getHandle").invoke(world); // Using reflection allows this to work with all Minecraft versions
        return worldServer.getChunkProvider().getChunkGenerator();
    }

    public static Thread inject(World world, StrongholdConfigWrapper config) throws ReflectiveOperationException {
        return inject(world, config, false, null);
    }

    public static Thread inject(World world, StrongholdConfigWrapper config, boolean force) throws ReflectiveOperationException {
        return inject(world, config, force, null);
    }

    public static Thread inject(World world, StrongholdConfigWrapper config, Logger logger) throws ReflectiveOperationException {
        return inject(world, config, false, logger);
    }

    public static Thread inject(World world, StrongholdConfigWrapper config, boolean force, Logger logger) throws ReflectiveOperationException {
        ChunkGenerator chunkGenerator = getGeneratorFromWorld(world);
        return inject(chunkGenerator, config.getInternal(), world.getName(), force, logger);
    }

    public static Thread regenerate(World world) throws ReflectiveOperationException {
        return regenerate(world, null);
    }

    public static Thread regenerate(World world, Logger logger) throws ReflectiveOperationException {
        ChunkGenerator chunkGenerator = getGeneratorFromWorld(world);
        return regenerate(chunkGenerator, world.getName(), logger);
    }

    private static Thread inject(ChunkGenerator chunkGenerator, StructureSettingsStronghold config, String worldName, boolean force, Logger logger) throws ReflectiveOperationException {
        StructureSettings structuresConfig = chunkGenerator.getSettings();
        if (!force && structuresConfig.b() == null) return null; // No stronghold in this world
        STRONGHOLD_FIELD.set(structuresConfig, config);
        return regenerate(chunkGenerator, worldName, logger);
    }

    private static Thread regenerate(ChunkGenerator chunkGenerator, String worldName, Logger logger) throws ReflectiveOperationException {
        List<ChunkCoordIntPair> strongholds = STRONGHOLDS_FIELD.get(chunkGenerator);
        strongholds.clear();
        if (threadGroup == null) {
            regenerateInternal(chunkGenerator, strongholds, worldName, logger);
            return null;
        } else {
            List<ChunkCoordIntPair> synchronizedStrongholds = Collections.synchronizedList(strongholds);
            Thread thread = threadGroup.newThreadInGroup(() -> {
                try {
                    regenerateInternal(chunkGenerator, synchronizedStrongholds, worldName, logger);
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
            return thread;
        }
    }

    private static void regenerateInternal(ChunkGenerator chunkGenerator, List<ChunkCoordIntPair> into, String worldName, Logger logger) throws ReflectiveOperationException {
        if (logger == null) {
            StrongholdPositionGenerator.generateStrongholdPositions(chunkGenerator);
        } else {
            StrongholdPositionGenerator.generateStrongholdPositions(chunkGenerator, System.currentTimeMillis(), worldName, logger);
        }
    }

    public static boolean ensureLoadedCorrectly() {
        return loadException == null;
    }

    public static void failIfUnloaded() throws Exception {
        if (!ensureLoadedCorrectly()) {
            throw new RuntimeException("Unable to load StrongholdModifier", loadException);
        }
    }
}
