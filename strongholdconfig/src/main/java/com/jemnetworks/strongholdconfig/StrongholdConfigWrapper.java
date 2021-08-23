package com.jemnetworks.strongholdconfig;

import java.lang.reflect.InvocationTargetException;

import net.minecraft.world.level.levelgen.feature.configurations.StructureSettingsStronghold;

public final class StrongholdConfigWrapper {
    private final StructureSettingsStronghold internal;

    public StrongholdConfigWrapper(int distance, int spread, int count) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        this(new StructureSettingsStronghold(distance, spread, count));
    }

    public StrongholdConfigWrapper(Object wrapped) {
        internal = (StructureSettingsStronghold)wrapped;
    }

    public int getDistance() {
        return internal.a();
    }

    public int getSpread() {
        return internal.b();
    }

    public int getCount() {
        return internal.c();
    }

    public StructureSettingsStronghold getInternal() {
        return internal;
    }

    public boolean equals(Object o) {
        if (!(o instanceof StrongholdConfigWrapper)) return false;
        StrongholdConfigWrapper other = (StrongholdConfigWrapper)o;
        return getDistance() == other.getDistance() &&
               getSpread() == other.getSpread() &&
               getCount() == other.getCount();
    }
}
