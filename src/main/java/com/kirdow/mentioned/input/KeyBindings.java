package com.kirdow.mentioned.input;

import net.minecraft.client.option.KeyBinding;

import java.util.function.Consumer;

public class KeyBindings {

    public static KeyBinding keyOpenConfig;

    public static void init() {
        keyOpenConfig = new KeyBinding("key.ktnmentioned.config.open", 'K', "key.categories.ktnmentioned");
    }

    public static void register(Consumer<KeyBinding> registry) {
        registry.accept(keyOpenConfig);
    }



}
