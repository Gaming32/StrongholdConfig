package com.jemnetworks.strongholdconfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public final class StrongholdConfigWrapper {
    private static Exception loadException = null;

    private static Class<?> CONFIG_CLASS;

    private static Field DISTANCE_FIELD;
    private static Field SPREAD_FIELD;
    private static Field COUNT_FIELD;

    private static Constructor<?> CONFIG_CONSTRUCTOR;

    static {
        try {
            CONFIG_CLASS = Class.forName("net.minecraft.world.gen.chunk.StrongholdConfig");

            DISTANCE_FIELD = CONFIG_CLASS.getDeclaredField("distance");
            SPREAD_FIELD = CONFIG_CLASS.getDeclaredField("spread");
            COUNT_FIELD = CONFIG_CLASS.getDeclaredField("count");

            DISTANCE_FIELD.setAccessible(true);
            SPREAD_FIELD.setAccessible(true);
            COUNT_FIELD.setAccessible(true);

            CONFIG_CONSTRUCTOR = CONFIG_CLASS.getConstructor(int.class, int.class, int.class);
        } catch (Exception e) {
            loadException = e;
        }
    }

    private final Object internal;

    public StrongholdConfigWrapper(int distance, int spread, int count) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        this(CONFIG_CONSTRUCTOR.newInstance(distance, spread, count));
    }

    private StrongholdConfigWrapper(Object wrapped) {
        internal = wrapped;
    }

    public int getDistance() throws IllegalArgumentException, IllegalAccessException {
        return DISTANCE_FIELD.getInt(internal);
    }

    public int getSpread() throws IllegalArgumentException, IllegalAccessException {
        return SPREAD_FIELD.getInt(internal);
    }

    public int getCount() throws IllegalArgumentException, IllegalAccessException {
        return COUNT_FIELD.getInt(internal);
    }

    public Object getInternalConfig() {
        return internal;
    }

    public static boolean ensureLoadedCorrectly() {
        return loadException == null;
    }

    public static void failIfUnloaded() throws Exception {
        if (!ensureLoadedCorrectly()) {
            throw loadException;
        }
    }
}
