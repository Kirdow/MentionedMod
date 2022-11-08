package com.kirdow.mentioned.util.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;

public class ReflectClass {

    private static final ReflectClass NULL = new ReflectClass(null);

    private static Map<Class<?>, ReflectClass> cache = new HashMap<>();
    public static ReflectClass forClass(Class<?> clazz) {
        if (clazz == null) return NULL;

        return cache.computeIfAbsent(clazz, key -> new ReflectClass(key));
    }

    public static ReflectClass forClass(String classname) {
        if (classname == null) return NULL;

        Class<?> clazz = null;
        try {
            clazz = Class.forName(classname);
        } catch (ClassNotFoundException ignored) {
        }

        return forClass(clazz);
    }

    public static ReflectClass forClass(String classname, Object...formatArgs) {
        if (classname == null) return NULL;

        try {
            classname = String.format(classname, formatArgs);
        } catch (IllegalFormatException ignored) {
            return NULL;
        }

        return forClass(classname);
    }

    public final Class<?> clazz;

    private ReflectClass(Class<?> clazz) {
        this.clazz = clazz;
    }

    public boolean isNull() {
        return clazz == null;
    }

    public boolean isValid() {
        return clazz != null;
    }

    public int getDeclaredFieldCount() {
        if (clazz == null) return 0;

        return clazz.getDeclaredFields().length;
    }

    public int getDeclaredMethodCount() {
        if (clazz == null) return 0;

        return clazz.getDeclaredMethods().length;
    }

    public int getFieldCount() {
        if (clazz == null) return 0;

        return clazz.getFields().length;
    }

    public int getMethodCount() {
        if (clazz == null) return 0;

        return clazz.getMethods().length;
    }

    public ReflectField getDeclaredFieldAt(int index) {
        if (clazz == null) return ReflectField.forField(null);

        try {
            Field field = clazz.getDeclaredFields()[index];
            return ReflectField.forField(field);
        } catch (ArrayIndexOutOfBoundsException ignored) {
            return ReflectField.forField(null);
        }
    }

    public ReflectMethod getDeclaredMethodAt(int index) {
        if (clazz == null) return ReflectMethod.forMethod(null);

        try {
            Method method = clazz.getDeclaredMethods()[index];
            return ReflectMethod.forMethod(method);
        } catch (ArrayIndexOutOfBoundsException ignored) {
            return ReflectMethod.forMethod(null);
        }
    }

    public ReflectField getFieldAt(int index) {
        if (clazz == null) return ReflectField.forField(null);

        try {
            Field field = clazz.getFields()[index];
            return ReflectField.forField(field);
        } catch (ArrayIndexOutOfBoundsException ignored) {
            return ReflectField.forField(null);
        }
    }

    public ReflectMethod getMethodAt(int index) {
        if (clazz == null) return ReflectMethod.forMethod(null);

        try {
            Method method = clazz.getMethods()[index];
            return ReflectMethod.forMethod(method);
        } catch (ArrayIndexOutOfBoundsException ignored) {
            return ReflectMethod.forMethod(null);
        }
    }

    public ReflectField getDeclaredFieldByName(String name) {
        if (clazz == null || name == null) return ReflectField.forField(null);

        try {
            Field field = clazz.getDeclaredField(name);
            return ReflectField.forField(field);
        } catch (NoSuchFieldException e) {
            return ReflectField.forField(null);
        }
    }

    public ReflectMethod getDeclaredMethodByName(String name, Class<?>...classes) {
        if (clazz == null || classes == null) return ReflectMethod.forMethod(null);

        try {
            Method method = clazz.getDeclaredMethod(name, classes);
            return ReflectMethod.forMethod(method);
        } catch (NoSuchMethodException e) {
            return ReflectMethod.forMethod(null);
        }
    }

    public ReflectField getFieldByName(String name) {
        if (clazz == null || name == null) return ReflectField.forField(null);

        try {
            Field field = clazz.getField(name);
            return ReflectField.forField(field);
        } catch (NoSuchFieldException e) {
            return ReflectField.forField(null);
        }
    }

    public ReflectMethod getMethodByName(String name, Class<?>...classes) {
        if (clazz == null || classes == null) return ReflectMethod.forMethod(null);

        try {
            Method method = clazz.getMethod(name, classes);
            return ReflectMethod.forMethod(method);
        } catch (NoSuchMethodException e) {
            return ReflectMethod.forMethod(null);
        }
    }

    public boolean clone(Object src, Object dst) {
        Class<?> clazz = this.clazz;

        Field modifiersField = null;
        try {
            modifiersField = Field.class.getDeclaredField("modifiers");
        } catch (NoSuchFieldException e) {
            return false;
        }
        modifiersField.setAccessible(true);
        do {
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);

                try {
                    modifiersField.setInt(field, modifiersField.getInt(field) & ~Modifier.FINAL);
                    field.set(dst, field.get(src));
                } catch (IllegalAccessException ignored) {
                }
            }
        } while ((clazz = clazz.getSuperclass()) != Object.class);

        return true;
    }

    public boolean is(Class<?> clazz) {
        return is(forClass(clazz));
    }

    public boolean isExact(Class<?> clazz) {
        return isExact(forClass(clazz));
    }

    public boolean is(ReflectClass clazz) {
        return this.clazz.isAssignableFrom(clazz.clazz);
    }

    public boolean isExact(ReflectClass clazz) {
        return this.clazz == clazz.clazz;
    }

}
