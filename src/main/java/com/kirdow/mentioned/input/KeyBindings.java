package com.kirdow.mentioned.input;

import net.minecraft.client.KeyMapping;

import java.util.function.Consumer;

public class KeyBindings {

    public static KeyMapping keyOpenConfig;

    public static void init() {
        keyOpenConfig = new KeyMapping("key.ktnmentioned.config.open", 'K', "key.categories.ktnmentioned");
    }

    public static void register(Consumer<KeyMapping> registry) {
        registry.accept(keyOpenConfig);
    }



}
