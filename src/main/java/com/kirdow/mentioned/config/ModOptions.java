package com.kirdow.mentioned.config;

import com.kirdow.mentioned.Mentioned;
import net.minecraft.util.Formatting;

import java.io.File;

public class ModOptions {

    public String[] filters;
    public boolean filterSelf;
    public int delay;
    public String color;
    public boolean useColor;
    public boolean useBold;
    public boolean useItalic;
    public boolean useStrikeThrough;
    public boolean useUnderline;

    public ModOptions() {
        filters = new String[0];
        filterSelf = true;
        delay = 500;
        color = "gold";
        useColor = true;
        useBold = true;
        useItalic = false;
        useStrikeThrough = false;
        useUnderline = false;
    }

    public static final ConfigSpec.Accessor<String[]> filtersValue;
    public static final ConfigSpec.Accessor<Boolean> filterSelfValue;
    public static final ConfigSpec.Accessor<Integer> delayValue;
    public static final ConfigSpec.Accessor<Formatting> colorValue;
    public static final ConfigSpec.Accessor<Boolean> useColorValue;
    public static final ConfigSpec.Accessor<Boolean> useBoldValue;
    public static final ConfigSpec.Accessor<Boolean> useItalicValue;
    public static final ConfigSpec.Accessor<Boolean> useStrikeThroughValue;
    public static final ConfigSpec.Accessor<Boolean> useUnderlineValue;

    public static final ConfigSpec<ModOptions> CLIENT;

    static {
        File file = Mentioned.getModPath().resolve("ktnmentioned.json").toFile();
        boolean fileExists = file.exists();
        CLIENT = new ConfigSpec<>(file, ModOptions.class);
        if (!fileExists) {
            // Copy over old config
            ModConfig.registerConfig();
            CLIENT.set(opts -> {
                opts.filters = ModConfig.FILTERS.toArray(new String[0]);
                opts.filterSelf = ModConfig.FILTER_SELF;
                opts.delay = (int)ModConfig.DELAY;
                opts.color = getColorFromFormatting(ModConfig.COLOR);
                opts.useColor = ModConfig.STYLE_COLOR;
                opts.useBold = ModConfig.STYLE_BOLD;
                opts.useItalic = ModConfig.STYLE_ITALIC;
                opts.useStrikeThrough = ModConfig.STYLE_STRIKETHROUGH;
                opts.useUnderline = ModConfig.STYLE_UNDERLINE;
            });
        }

        filtersValue = CLIENT.createAccessor((v) -> CLIENT.set(c -> c.filters = v), () -> CLIENT.get(new String[0], c -> c.filters));
        filterSelfValue = CLIENT.createAccessor((v) -> CLIENT.set(c -> c.filterSelf = v), () -> CLIENT.get(true, c -> c.filterSelf));
        delayValue = CLIENT.createAccessor((v) -> CLIENT.set(c -> c.delay = v), () -> CLIENT.get(500, c -> c.delay));
        colorValue = CLIENT.createAccessor((v) -> CLIENT.set(c -> c.color = getColorFromFormatting(v)), () -> getFormattingFromString(CLIENT.get(getColorFromFormatting(Formatting.GOLD), c -> c.color)));
        useColorValue = CLIENT.createAccessor((v) -> CLIENT.set(c -> c.useColor = v), () -> CLIENT.get(true, c -> c.useColor));
        useBoldValue = CLIENT.createAccessor((v) -> CLIENT.set(c -> c.useBold = v), () -> CLIENT.get(true, c -> c.useBold));
        useItalicValue = CLIENT.createAccessor((v) -> CLIENT.set(c -> c.useItalic = v), () -> CLIENT.get(true, c -> c.useItalic));
        useStrikeThroughValue = CLIENT.createAccessor((v) -> CLIENT.set(c -> c.useStrikeThrough = v), () -> CLIENT.get(false, c -> c.useStrikeThrough));
        useUnderlineValue = CLIENT.createAccessor((v) -> CLIENT.set(c -> c.useUnderline = v), () -> CLIENT.get(false, c -> c.useUnderline));
    }

    private static String getColorFromFormatting(Formatting color) {
        if (!color.isColor()) {
            return getColorFromFormatting(Formatting.GOLD);
        }

        return color.getName().toLowerCase().replaceAll("[^a-z]", "");
    }

    private static Formatting getFormattingFromString(String color) {
        Formatting formatting = Formatting.byName(color);
        if (formatting == null || !formatting.isColor()) return Formatting.GOLD;
        return formatting;
    }

}
