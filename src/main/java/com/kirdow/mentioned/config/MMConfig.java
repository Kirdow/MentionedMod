package com.kirdow.mentioned.config;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class MMConfig {

    @Comment("Filters")
    public List<String> filter = new ArrayList<>();
    @Comment("Filter Self")
    public boolean filterSelf = true;
    @Comment("Delay")
    public long delay = 500;

    @Comment("Color")
    public Formatting color = Formatting.GOLD;

    @Comment("Style Color")
    public boolean styleColor = true;
    @Comment("Style Bold")
    public boolean styleBold = true;
    @Comment("Style Italic")
    public boolean styleItalic = false;
    @Comment("Style Strikethrough")
    public boolean styleStrikethrough = false;
    @Comment("Style Underline")
    public boolean styleUnderline = false;

}
