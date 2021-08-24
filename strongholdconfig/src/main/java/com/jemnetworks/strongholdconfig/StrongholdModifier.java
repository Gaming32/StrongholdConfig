package com.jemnetworks.strongholdconfig;

import java.lang.reflect.Field;
import java.util.List;

import com.jemnetworks.strongholdconfig.util.FieldHelper;

import org.bukkit.World;

import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.configurations.StructureSettingsStronghold;

public class StrongholdModifier {
    private static Throwable loadException = null;

    private static Field STRONGHOLD_FIELD;

    private static Field STRONGHOLDS_FIELD;

    static {
        try {
            STRONGHOLD_FIELD = StructureSettings.class.getDeclaredField("e");
            STRONGHOLD_FIELD.setAccessible(true);
            FieldHelper.makeNonFinal(STRONGHOLD_FIELD);

            STRONGHOLDS_FIELD = ChunkGenerator.class.getDeclaredField("f");
            STRONGHOLDS_FIELD.setAccessible(true);
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

    public static long inject(World world, StrongholdConfigWrapper config) throws ReflectiveOperationException {
        ChunkGenerator chunkGenerator = getGeneratorFromWorld(world);
        return inject(chunkGenerator, config.getInternal());
    }

    public static long regenerate(World world) throws ReflectiveOperationException {
        ChunkGenerator chunkGenerator = getGeneratorFromWorld(world);
        return regenerate(chunkGenerator);
    }

    private static long inject(ChunkGenerator chunkGenerator, StructureSettingsStronghold config) throws ReflectiveOperationException {
        StructureSettings structuresConfig = chunkGenerator.getSettings();
        if (structuresConfig.b() == null) return -1; // No stronghold in this world
        STRONGHOLD_FIELD.set(structuresConfig, config);
        return regenerate(chunkGenerator);
    }

    @SuppressWarnings("unchecked")
    private static long regenerate(ChunkGenerator chunkGenerator) throws ReflectiveOperationException {
        ((List<ChunkCoordIntPair>)STRONGHOLDS_FIELD.get(chunkGenerator)).clear();
        long start = System.currentTimeMillis();
        StrongholdPositionGenerator.generateStrongholdPositions(chunkGenerator);
        long end = System.currentTimeMillis();
        return end - start;
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
