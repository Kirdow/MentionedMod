package com.kirdow.mentioned.util.reflect;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ReflectField {

    private static final ReflectField NULL = new ReflectField(null);

    private static Map<Field, ReflectField> cache = new HashMap<>();
    public static ReflectField forField(Field field) {
        if (field == null) return null;

        return cache.computeIfAbsent(field, key -> new ReflectField(key));
    }

    public final Field field;
    private ReflectField(Field field) {
        this.field = field;
    }

    public boolean isNull() {
        return field == null;
    }

    public boolean isValid() {
        return field != null;
    }

    public ReflectClass getParent() {
        if (field == null) return ReflectClass.forClass((Class)null);

        return ReflectClass.forClass(field.getDeclaringClass());
    }

    public ReflectClass getType() {
        if (field == null) return ReflectClass.forClass((Class)null);

        return ReflectClass.forClass(field.getType());
    }

    public <T> T get(Object owner) {
        Object obj = null;

        field.setAccessible(true);
        try {
            obj = field.get(owner);
        } catch (IllegalAccessException ignored) {
        }

        return (T)obj;
    }

    public <T> void set(Object owner, T value) {
        field.setAccessible(true);
        try {
            field.set(owner, value);
        } catch (IllegalAccessException ignored) {
        }
    }

    public boolean move(Object src, Object dst) {
        field.setAccessible(true);
        try {
            Object obj = field.get(src);
            field.set(dst, obj);

            return true;
        } catch (IllegalAccessException ignored) {
            return false;
        }
    }

}
