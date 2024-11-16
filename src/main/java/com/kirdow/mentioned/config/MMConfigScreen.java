package com.kirdow.mentioned.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.kirdow.mentioned.Mentioned.wildcard;

public class MMConfigScreen {

    private static final String[] COLORS;
    private static final Map<String, Formatting> COLOR_MAP;

    public static Screen getConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable(wildcard("config.<id>.base.modname")))
                .setDoesConfirmSave(true)
                .transparentBackground();

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        builder.getOrCreateCategory(Text.translatable(wildcard("config.<id>.section.filter")))
                .addEntry(entryBuilder.startStrList(Text.translatable(wildcard("config.<id>.filters.list.short")), ConfigManager.get().filter)
                        .setDefaultValue(new ArrayList<>())
                        .setTooltip(Text.translatable(wildcard("config.<id>.filters.list.long")))
                        .setSaveConsumer(value -> ConfigManager.get().filter = value)
                        .build())
                .addEntry(entryBuilder.startBooleanToggle(Text.translatable(wildcard("config.<id>.filters.self.short")), ConfigManager.get().filterSelf)
                        .setDefaultValue(true)
                        .setTooltip(Text.translatable(wildcard("config.<id>.filters.self.long")))
                        .setSaveConsumer(value -> ConfigManager.get().filterSelf = value)
                        .build());

        builder.getOrCreateCategory(Text.translatable(wildcard("config.<id>.section.misc")))
                .addEntry(entryBuilder.startLongField(Text.translatable(wildcard("config.<id>.misc.delay.short")), ConfigManager.get().delay)
                        .setDefaultValue(500L)
                        .setTooltip(Text.translatable(wildcard("config.<id>.misc.delay.long")))
                        .setSaveConsumer(value -> ConfigManager.get().delay = (value < 0L) ? 0L : value)
                        .build());

        builder.getOrCreateCategory(Text.translatable(wildcard("config.<id>.section.style")))
                .addEntry(entryBuilder.startSelector(Text.translatable(wildcard("config.<id>.style.color.code.short")), COLORS, COLORS[ConfigManager.get().color.ordinal()])
                        .setDefaultValue(COLORS[Formatting.GOLD.ordinal()])
                        .setTooltip(Text.translatable(wildcard("config.<id>.style.color.code.long")))
                        .setSaveConsumer(value -> ConfigManager.get().color = COLOR_MAP.getOrDefault(value, Formatting.GOLD))
                        .build())
                .addEntry(entryBuilder.startBooleanToggle(Text.translatable(wildcard("config.<id>.style.color.short")), ConfigManager.get().styleColor)
                        .setDefaultValue(true)
                        .setTooltip(Text.translatable(wildcard("config.<id>.style.color.long")))
                        .setSaveConsumer(value -> ConfigManager.get().styleColor = value)
                        .build())
                .addEntry(entryBuilder.startBooleanToggle(Text.translatable(wildcard("config.<id>.style.bold.short")), ConfigManager.get().styleBold)
                        .setDefaultValue(true)
                        .setTooltip(Text.translatable(wildcard("config.<id>.style.bold.long")))
                        .setSaveConsumer(value -> ConfigManager.get().styleBold = value)
                        .build())
                .addEntry(entryBuilder.startBooleanToggle(Text.translatable(wildcard("config.<id>.style.italic.short")), ConfigManager.get().styleItalic)
                        .setDefaultValue(false)
                        .setTooltip(Text.translatable(wildcard("config.<id>.style.italic.long")))
                        .setSaveConsumer(value -> ConfigManager.get().styleItalic = value)
                        .build())
                .addEntry(entryBuilder.startBooleanToggle(Text.translatable(wildcard("config.<id>.style.strikethrough.short")), ConfigManager.get().styleStrikethrough)
                        .setDefaultValue(false)
                        .setTooltip(Text.translatable(wildcard("config.<id>.style.strikethrough.long")))
                        .setSaveConsumer(value -> ConfigManager.get().styleStrikethrough = value)
                        .build())
                .addEntry(entryBuilder.startBooleanToggle(Text.translatable(wildcard("config.<id>.style.underline.short")), ConfigManager.get().styleUnderline)
                        .setDefaultValue(false)
                        .setTooltip(Text.translatable(wildcard("config.<id>.style.underline.long")))
                        .setSaveConsumer(value -> ConfigManager.get().styleUnderline = value)
                        .build());

        builder.setSavingRunnable(ConfigManager::saveConfig);

        return builder.build();
    }

    static {
        String[] colors = new String[]{
                "Black",
                "Dark Blue",
                "Dark Green",
                "Dark Aqua",
                "Dark Red",
                "Dark Purple",
                "Gold",
                "Gray",
                "Dark Gray",
                "Blue",
                "Green",
                "Aqua",
                "Red",
                "Light Purple",
                "Yellow",
                "White"
        };

        COLORS = new String[colors.length];
        COLOR_MAP = new HashMap<>();
        for (int i = 0; i < colors.length; i++) {
            String color = String.format("\u00A7%s%s", Integer.toHexString(i), colors[i]);
            COLOR_MAP.put(color, Formatting.values()[i]);
            COLORS[i] = color;
        }
    }

}
