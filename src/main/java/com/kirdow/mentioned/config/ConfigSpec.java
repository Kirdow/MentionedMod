package com.kirdow.mentioned.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kirdow.mentioned.Logger;
import com.kirdow.mentioned.util.Utils;

import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConfigSpec<T> {

    public static class Accessor<R> {
        private Consumer<R> setter;
        private Supplier<R> getter;

        public Accessor(Consumer<R> setter, Supplier<R> getter) {
            this.setter = setter;
            this.getter = getter;
        }

        public R get() {
            return getter.get();
        }

        public void set(R value) {
            setter.accept(value);
        }
    }

    private File file;
    private Class<T> clazz;
    private T data;

    public ConfigSpec(File file, Class<T> clazz) {
        this.file = file;
        this.clazz = clazz;

        this.load();
    }

    public void load() {
        try (Reader reader = new FileReader(file)) {
            Gson gson = new GsonBuilder().create();
            T result = gson.fromJson(reader, clazz);
            data = result;
        } catch (IOException ignored) {
            if (data == null) {
                try {
                    var construct = clazz.getConstructor();
                    construct.setAccessible(true);
                    data = construct.newInstance();
                } catch (Throwable ex1) {
                    Logger.error("Failed to load and create config!");
                    ex1.printStackTrace();
                }
            }
        }
    }

    private AtomicInteger req = new AtomicInteger(0);
    public void save() {
        if (req.incrementAndGet() == 1) {
            Utils.runAsync(() -> {
                do {
                    saveImpl();
                } while (req.decrementAndGet() > 0);
            });
        }
    }

    private void saveImpl() {
        try (Writer writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(data, writer);
        } catch (IOException ex) {
        }
    }

    public <R> R get(R _default, Function<T, R> func) {
        try {
            return func.apply(data);
        } catch (Throwable ignored) {
        }

        return _default;
    }

    public void set(Consumer<T> consumer) {
        try {
            consumer.accept(data);
        } catch (Throwable ignored) {
            return;
        }

        save();
    }

    public <R> Accessor<R> createAccessor(Consumer<R> setter, Supplier<R> getter) {
        return new Accessor<R>(setter, getter);
    }

}

