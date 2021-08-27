package com.jemnetworks.strongholdconfig.util;

import java.lang.reflect.Field;

public final class TypedField<T> {
    private final Field field;

    private TypedField(Field field) {
        this.field = field;
    }

    public static <T> TypedField<T> getDeclaredField(Class<?> clazz, String name) throws NoSuchFieldException, SecurityException {
        return new TypedField<>(clazz.getDeclaredField(name));
    }

    @SuppressWarnings("unchecked")
    public T get(Object obj) throws IllegalArgumentException, IllegalAccessException {
        return (T)field.get(obj);
    }

    public void set(Object obj, T value) throws IllegalArgumentException, IllegalAccessException {
        field.set(obj, value);
    }

    public Field getWrapped() {
        return field;
    }
}
