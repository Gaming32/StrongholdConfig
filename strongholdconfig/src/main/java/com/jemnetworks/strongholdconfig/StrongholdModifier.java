package com.jemnetworks.strongholdconfig;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.World;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class StrongholdModifier {
    private static Exception loadException = null;

    private static Class<?> STRUCTURES_CONFIG_CLASS;
    private static Object DEFAULT_STRONGHOLD_FIELD_BASE;
    private static long DEFAULT_STRONGHOLD_FIELD_OFFSET;
    private static Field STRONGHOLD_FIELD;

    private static Class<?> CHUNK_GENERATOR_CLASS;
    private static Field STRUCTURES_CONFIG_FIELD;
    private static Field STRONGHOLDS_FIELD;
    private static Method GENERATE_STRONGHOLD_METHOD;

    private static Unsafe UNSAFE;

    static {
        try {
            STRUCTURES_CONFIG_CLASS = Class.forName("net.minecraft.world.level.levelgen.StructureSettings");
            CHUNK_GENERATOR_CLASS = Class.forName("net.minecraft.world.level.chunk.ChunkGenerator");

            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            UNSAFE = (Unsafe)unsafeField.get(null);

            Field defaultStronholdField = STRUCTURES_CONFIG_CLASS.getDeclaredField("c");
            DEFAULT_STRONGHOLD_FIELD_BASE = UNSAFE.staticFieldBase(defaultStronholdField);
            DEFAULT_STRONGHOLD_FIELD_OFFSET = UNSAFE.staticFieldOffset(defaultStronholdField);
            STRONGHOLD_FIELD = STRUCTURES_CONFIG_CLASS.getDeclaredField("e");
            STRONGHOLD_FIELD.setAccessible(true);
            FieldHelper.makeNonFinal(STRONGHOLD_FIELD);

            STRUCTURES_CONFIG_FIELD = CHUNK_GENERATOR_CLASS.getDeclaredField("d");
            STRONGHOLDS_FIELD = CHUNK_GENERATOR_CLASS.getDeclaredField("f");
            GENERATE_STRONGHOLD_METHOD = CHUNK_GENERATOR_CLASS.getDeclaredMethod("h");
            STRUCTURES_CONFIG_FIELD.setAccessible(true);
            STRONGHOLDS_FIELD.setAccessible(true);
            GENERATE_STRONGHOLD_METHOD.setAccessible(true);
        } catch (Exception e) {
            loadException = e;
        }
    }

    public static StrongholdConfigWrapper getDefaultConfig() {
        return new StrongholdConfigWrapper(UNSAFE.getObject(DEFAULT_STRONGHOLD_FIELD_BASE, DEFAULT_STRONGHOLD_FIELD_OFFSET));
    }

    public static void injectDefault(StrongholdConfigWrapper config) {
        UNSAFE.putObject(DEFAULT_STRONGHOLD_FIELD_BASE, DEFAULT_STRONGHOLD_FIELD_OFFSET, config.getNMS());
    }

    private static Object getGeneratorFromWorld(World world) throws ReflectiveOperationException {
        Object worldServer = world.getClass().getDeclaredMethod("getHandle").invoke(world);
        Object chunkProvider = worldServer.getClass().getDeclaredMethod("getChunkProvider").invoke(worldServer);
        return chunkProvider.getClass().getDeclaredMethod("getChunkGenerator").invoke(chunkProvider);
    }

    public static long inject(World world, StrongholdConfigWrapper config) throws ReflectiveOperationException {
        Object chunkGenerator = getGeneratorFromWorld(world);
        return inject(chunkGenerator, config.getNMS());
    }

    public static long regenerate(World world) throws ReflectiveOperationException {
        Object chunkGenerator = getGeneratorFromWorld(world);
        return regenerate(chunkGenerator);
    }

    private static long inject(Object chunkGenerator, Object config) throws ReflectiveOperationException {
        Object structuresConfig = STRUCTURES_CONFIG_FIELD.get(chunkGenerator);
        if (STRONGHOLD_FIELD.get(structuresConfig) == null) return -1; // No stronghold in this world
        STRONGHOLD_FIELD.set(structuresConfig, config);
        return regenerate(chunkGenerator);
    }

    private static long regenerate(Object chunkGenerator) throws ReflectiveOperationException {
        ((List<?>)STRONGHOLDS_FIELD.get(chunkGenerator)).clear();
        long start = System.currentTimeMillis();
        GENERATE_STRONGHOLD_METHOD.invoke(chunkGenerator);
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
