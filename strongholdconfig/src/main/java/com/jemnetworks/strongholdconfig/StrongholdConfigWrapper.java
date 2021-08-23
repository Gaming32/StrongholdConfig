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
            CONFIG_CLASS = Class.forName("net.minecraft.world.level.levelgen.feature.configurations.StructureSettingsStronghold");

            DISTANCE_FIELD = CONFIG_CLASS.getDeclaredField("b");
            SPREAD_FIELD = CONFIG_CLASS.getDeclaredField("c");
            COUNT_FIELD = CONFIG_CLASS.getDeclaredField("d");

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

    public StrongholdConfigWrapper(Object wrapped) {
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

    public Object getNMS() {
        return internal;
    }

    public boolean equals(Object o) {
        if (!(o instanceof StrongholdConfigWrapper)) return false;
        StrongholdConfigWrapper other = (StrongholdConfigWrapper)o;
        try {
            return getDistance() == other.getDistance() &&
                   getSpread() == other.getSpread() &&
                   getCount() == other.getCount();
        } catch (IllegalArgumentException | IllegalAccessException e) {
            return getNMS() == other.getNMS();
        }
    }

    public static boolean ensureLoadedCorrectly() {
        return loadException == null;
    }

    public static void failIfUnloaded() {
        if (!ensureLoadedCorrectly()) {
            throw new RuntimeException("Unable to load StrongholdConfigWrapper", loadException);
        }
    }
}
