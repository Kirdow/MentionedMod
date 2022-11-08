package com.kirdow.mentioned.util.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ReflectMethod {

    private static final ReflectMethod NULL = new ReflectMethod(null);

    private static Map<Method, ReflectMethod> cache = new HashMap<>();

    public static ReflectMethod forMethod(Method method) {
        if (method == null) return null;

        return cache.computeIfAbsent(method, key -> new ReflectMethod(key));
    }

    public final Method method;

    private ReflectMethod(Method method) {
        this.method = method;
    }

    public boolean isNull() {
        return method == null;
    }

    public boolean isValid() {
        return method != null;
    }

    public ReflectClass getParent() {
        if (method == null) return ReflectClass.forClass((Class)null);

        return ReflectClass.forClass(method.getDeclaringClass());
    }

    public ReflectClass getReturnType() {
        if (method == null) return ReflectClass.forClass((Class)null);

        return ReflectClass.forClass(method.getReturnType());
    }

    public ReflectClass[] getParameterTypes() {
        if (method == null) return new ReflectClass[0];

        return Arrays.stream(method.getParameterTypes()).map(ReflectClass::forClass).collect(Collectors.toList()).toArray(new ReflectClass[0]);
    }

    public <T> T invoke(Object owner, Object...args) {
        if (method == null) return (T)null;
        Object obj = null;

        method.setAccessible(true);
        try {
            obj = method.invoke(owner, args);
        } catch (IllegalAccessException ignored) {
        } catch (InvocationTargetException e) {
        }

        return (T)obj;
    }

}
