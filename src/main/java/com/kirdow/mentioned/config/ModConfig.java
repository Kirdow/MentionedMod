package com.kirdow.mentioned.config;

import com.kirdow.mentioned.Mentioned;
import net.minecraft.ChatFormatting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ModConfig {
    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;

    public static List<String> FILTERS;
    public static boolean FILTER_SELF;

    public static long DELAY;

    public static ChatFormatting COLOR;

    public static boolean STYLE_COLOR;
    public static boolean STYLE_BOLD;
    public static boolean STYLE_ITALIC;
    public static boolean STYLE_STRIKETHROUGH;
    public static boolean STYLE_UNDERLINE;

    public static void registerConfig() {
        configs = new ModConfigProvider();
        createConfig();

        CONFIG = SimpleConfig.of(Mentioned.MOD_ID + "_config").provider(configs).request();

        assignConfig();
    }

    private static void createConfig() {
        configs.addComment(
                "This config file is for Mentioned!",
                "Edit values to fit your preference."
        );
        configs.skip();

        configs.addComment("This is the text that will be searched for in chat.");
        configs.addKeyValuePair("key.filter", "", "Separate each with a semicolon ;");
        configs.addKeyValuePair("key.filter.self", true, "This will also include your playername in the filter");
        configs.skip();

        configs.addComment("This is the delay that will be used to limit amount of pings at once!");
        configs.addKeyValuePair("key.delay", 500, "Delay is milliseconds (0=disabled)");
        configs.skip();

        configs.addComment("This is the color a message will have if a message match the filter.");
        configs.addKeyValuePair("key.color", "gold", "Supported colors are: aqua, black, blue, dark aqua, dark blue, dark gray, dark grey, dark green, dark purple, dark red, gold, gray, grey, green, light purple, red and yellow.");
        configs.skip();

        configs.addComment("Styling options. This will affect the style of a matching message!");
        configs.addKeyValuePair("key.style.color", true, "The message will appear in the color specified above");
        configs.addKeyValuePair("key.style.bold", true, "The message will appear bold");
        configs.addKeyValuePair("key.style.italic", false, "The message will appear italic");
        configs.addKeyValuePair("key.style.strikethrough", false, "The message will appear strikethrough");
        configs.addKeyValuePair("key.style.underline", false, "The message will appear underlined");
    }

    private static void assignConfig() {
        FILTERS = getNamesFromConfig(CONFIG.getOrDefault("key.filter", ""));
        FILTER_SELF = CONFIG.getOrDefault("key.filter.self", true);
        DELAY = CONFIG.getOrDefault("key.delay", 500);
        COLOR = getColorFromConfig(CONFIG.getOrDefault("key.color", "gold"));
        STYLE_COLOR = CONFIG.getOrDefault("key.style.color", true);
        STYLE_BOLD = CONFIG.getOrDefault("key.style.bold", true);
        STYLE_ITALIC = CONFIG.getOrDefault("key.style.italic", false);
        STYLE_STRIKETHROUGH = CONFIG.getOrDefault("key.style.strikethrough", false);
        STYLE_UNDERLINE = CONFIG.getOrDefault("key.style.underline", false);

    }

    private static List<String> getNamesFromConfig(String input) {
        return input.isEmpty() ? Collections.emptyList() : Arrays.asList(input.split(";"));
    }

    private static ChatFormatting getColorFromConfig(String input) {
        if (input == null || input.isEmpty()) return ChatFormatting.GOLD;

        return switch (input) {
            case "aqua" -> ChatFormatting.AQUA;
            case "black" -> ChatFormatting.BLACK;
            case "blue" -> ChatFormatting.BLUE;
            case "dark aqua" -> ChatFormatting.DARK_AQUA;
            case "dark blue" -> ChatFormatting.DARK_BLUE;
            case "dark gray", "dark_grey" -> ChatFormatting.DARK_GRAY;
            case "dark green" -> ChatFormatting.DARK_GREEN;
            case "dark purple" -> ChatFormatting.DARK_PURPLE;
            case "dark red" -> ChatFormatting.DARK_RED;
            case "gray", "grey" -> ChatFormatting.GRAY;
            case "green" -> ChatFormatting.GREEN;
            case "light purple", "purple" -> ChatFormatting.LIGHT_PURPLE;
            case "red" -> ChatFormatting.RED;
            case "yellow" -> ChatFormatting.YELLOW;
            default -> ChatFormatting.GOLD;
        };
    }

}
