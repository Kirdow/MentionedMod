package com.kirdow.mentioned.config.gui;

import net.minecraftforge.client.ConfigScreenHandler;

public class ConfigMenuHelper {

    public static ConfigScreenHandler.ConfigScreenFactory createConfigGuiFactory() {
        return new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> new MentionedConfigMenu(screen));
    }

}
