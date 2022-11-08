package com.kirdow.mentioned.config.gui;

import net.minecraftforge.client.ConfigGuiHandler;

public class ConfigMenuHelper {

    public static ConfigGuiHandler.ConfigGuiFactory createConfigGuiFactory() {
        return new ConfigGuiHandler.ConfigGuiFactory((minecraft, screen) -> new MentionedConfigMenu(screen));
    }

}
