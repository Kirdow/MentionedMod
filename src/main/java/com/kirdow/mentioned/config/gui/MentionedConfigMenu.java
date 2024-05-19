package com.kirdow.mentioned.config.gui;

import com.kirdow.mentioned.config.ConfigSpec;
import com.kirdow.mentioned.config.ModOptions;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.FormattedCharSequence;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MentionedConfigMenu extends Screen {

    private class ButtonDelegate<T> {
        int id;
        Button button;
        ConfigSpec.Accessor<T> option;
        Supplier<Boolean> delegate;
        Supplier<Component> messageDelegate;

        public ButtonDelegate(int id, Supplier<Boolean> delegate, Button button, ConfigSpec.Accessor<T> option, Supplier<Component> messageDelegate) {
            this.id = id;
            this.delegate = delegate;
            this.button = button;
            this.option = option;
            this.messageDelegate = messageDelegate;
        }
    }

    private int centerX, centerY, marginX, marginY;
    private int mouseX, mouseY;
    private Screen previousMenu;
    private EditBox filtersEditBox;

    private Component activeTooltip;

    private Map<Integer, ButtonDelegate> delegateMap = new HashMap<>();

    public MentionedConfigMenu() {
        this(null);
    }

    public MentionedConfigMenu(Screen previous) {
        super(Component.translatable("config.ktnmentioned.base.config_title"));
        previousMenu = previous;
    }

    private void pollDelegates() {
        for (var entry : delegateMap.entrySet()) {
            var value = entry.getValue();
            value.button.active = (Boolean)value.delegate.get();
        }

        for (var entry : delegateMap.entrySet()) {
            var value = entry.getValue();
            value.button.setMessage(value.button.active ? (Component)value.messageDelegate.get() : Component.literal(""));
        }
    }

    private String[] filters = new String[0];
    private String[] getFilters() {
        if (filters == null) {
            filters = ModOptions.filtersValue.get();
        }
        return filters;
    }

    @Override
    public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        activeTooltip = null;
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        renderBackground(ms, 0);
        var titleText = Component.translatable("config.ktnmentioned.base.config_title");
        var font = Minecraft.getInstance().font;
        font.drawShadow(ms, titleText, (width - font.width(titleText.getString())) / 2, 20, 0xf0f0f0);

        generatePageEntry(0, pos -> drawButtonPrefix(ms, 0, "config.ktnmentioned.section.general", null));
        generatePageEntry(1, pos -> drawButtonPrefix(ms, 1, "config.ktnmentioned.filter.filters.short", "config.ktnmentioned.filter.filters.long"));
        generatePageEntry(3, pos -> drawButtonPrefix(ms, 3, "config.ktnmentioned.filter.self.short", "config.ktnmentioned.filter.self.long"));
        generatePageEntry(4, pos -> drawButtonPrefix(ms, 4, "config.ktnmentioned.section.style", null));
        generatePageEntry(5, pos -> drawButtonPrefix(ms, 5, "config.ktnmentioned.style.enable.color.short", "config.ktnmentioned.style.enable.color.long"));
        generatePageEntry(6, pos -> drawButtonPrefix(ms, 6, "config.ktnmentioned.style.color.short", "config.ktnmentioned.style.color.long"));
        generatePageEntry(7, pos -> drawButtonPrefix(ms, 7, "config.ktnmentioned.style.enable.bold.short", "config.ktnmentioned.style.enable.bold.long"));
        generatePageEntry(8, pos -> drawButtonPrefix(ms, 8, "config.ktnmentioned.style.enable.italic.short", "config.ktnmentioned.style.enable.italic.long"));
        generatePageEntry(9, pos -> drawButtonPrefix(ms, 9, "config.ktnmentioned.style.enable.strike.short", "config.ktnmentioned.style.enable.strike.long"));
        generatePageEntry(10, pos -> drawButtonPrefix(ms, 10, "config.ktnmentioned.style.enable.under.short", "config.ktnmentioned.style.enable.under.long"));

        super.render(ms, mouseX, mouseY, partialTicks);

        if (activeTooltip != null) {
            List<FormattedCharSequence> hoverTextLines = font.split(activeTooltip, 134);

            renderTooltip(ms, hoverTextLines, mouseX, mouseY);
        }

    }

    @Override
    public void init() {
        this.centerX = width / 2;
        this.centerY = height / 2;

        marginX = 10;
        marginY = 10;

        calculatePages(10);
        addButtons();
    }

    private void addButtons() {
        addButtonClickable(10, 10, () -> Component.translatable("gui.back"), btn -> onClose(), null);

        generatePageEntry(2, pos -> addButtonClickable(2, () -> Component.translatable("config.ktnmentioned.update"), btn -> {
            Set<String> filters = Arrays.stream(filtersEditBox.getValue().split(";")).collect(Collectors.toSet());
            ModOptions.filtersValue.set(filters.toArray(new String[0]));
            MentionedConfigMenu.this.filters = null;
        }, null));

        generatePageEntry(2, pos -> {
            filtersEditBox = new EditBox(Minecraft.getInstance().font, centerX - 200, getOptionPosition(2, false), 200, 20, null);
            addRenderableWidget(filtersEditBox);
            filtersEditBox.setValue(String.join(";", ModOptions.filtersValue.get()));
        });

        generatePageEntry(3, pos -> addButtonConfigToggle(3, ModOptions.filterSelfValue));
        generatePageEntry(5, pos -> addButtonConfigToggle(5, ModOptions.useColorValue));
        generatePageEntry(6, pos -> addButtonConfigColor(6, ModOptions.colorValue, () -> ModOptions.useColorValue.get()));
        generatePageEntry(7, pos -> addButtonConfigToggle(7, ModOptions.useBoldValue));
        generatePageEntry(8, pos -> addButtonConfigToggle(8, ModOptions.useItalicValue));
        generatePageEntry(9, pos -> addButtonConfigToggle(9, ModOptions.useStrikeThroughValue));
        generatePageEntry(10, pos -> addButtonConfigToggle(10, ModOptions.useUnderlineValue));

        addButtonClickable(Math.max(10, centerX - 400), height - 30, () -> Component.translatable("config.ktnmentioned.prev"), btn -> {
            if (checkPage(-1)) --currentPage;
            reloadPage();
        }, () -> checkPage(-1));
        addButtonClickable(Math.min(width - 110, centerX + 300), height - 30, () -> Component.translatable("config.ktnmentioned.next"), btn -> {
            if (checkPage(1)) ++currentPage;
            reloadPage();
        }, () -> checkPage(1));

        pollDelegates();
    }

    private void reloadPage() {
        filtersEditBox = null;
        clearWidgets();
        addButtons();
    }

    private int pageMin;
    private int pageMax;
    private int pageEnd;
    private int currentPage;

    private void calculatePages(int end) {
        int max = 0;
        for (int i = 0; ; ++i) {
            if (getOptionPosition(i, false) >= height) {
                max = i - 2;
                break;
            }
        }

        pageMin = 1;
        pageMax = max;
        pageEnd = end;
        currentPage = 0;
    }

    private int getPageStart() {
        int delta = pageMax - pageMin + 1;
        if (delta <= 0) return 0;
        return currentPage * delta;
    }

    private boolean checkPage(int pos) {
        int delta = pageMax - pageMin + 1;
        if (delta <= 0) return false;
        return (currentPage + pos >= 0) && ((currentPage + pos) * delta < pageEnd);
    }

    private void generatePageEntry(int pos, Consumer<Integer> callback) {
        if (callback == null) return;

        int delta = pageMax - pageMin + 1;
        if (delta <= 0) return;

        if (currentPage * delta >= pageEnd) currentPage = pageEnd / delta;
        int minPos = currentPage * delta;
        int maxPos = (currentPage + 1) * delta;

        if (pos >= minPos && pos < maxPos) {
            callback.accept(pos);
        }
    }

    protected void drawButtonPrefix(PoseStack ms, int pos, Object key, Object hover) {
        MutableComponent keyComp = null;
        if (key instanceof String keyString) {
            keyComp = Component.translatable(keyString);
        } else if (key instanceof MutableComponent mcomp) {
            keyComp = mcomp;
        } else if (key != null) {
            keyComp = Component.literal(key.toString());
        } else {
            return;
        }

        MutableComponent hoverComp = null;
        if (hover instanceof String hoverString) {
            hoverComp = Component.translatable(hoverString);
        } else if (hover instanceof MutableComponent mcomp) {
            hoverComp = mcomp;
        } else if (hover != null) {
            hoverComp = Component.literal(hover.toString());
        }

        var style = Style.EMPTY.withColor(ChatFormatting.WHITE);
        if (hoverComp != null) {
            style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComp.setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN))));
        }
        drawButtonPrefix(ms, keyComp.setStyle(style), pos, hoverComp == null);
    }

    protected void drawButtonPrefix(PoseStack ms, Component text, int pos, boolean isTitle) {
        var font = Minecraft.getInstance().font;

        int textWidth = font.width(text.getString());
        int x = centerX - (isTitle ? (textWidth / 2) : (textWidth + 10));
        int y = getOptionPosition(pos, true);

        font.drawShadow(ms, text, x, y, 0xf0f0f0);

        if (!(mouseX >= x - 10 && mouseY >= y - 10 && mouseX <= x + textWidth + 10 && mouseY <= y + font.lineHeight + 10))
            return;

        HoverEvent event = text.getStyle().getHoverEvent();
        if (event != null && event.getAction() == HoverEvent.Action.SHOW_TEXT) {
            activeTooltip = event.getValue(HoverEvent.Action.SHOW_TEXT);
        }

    }

    protected Button addButtonClickable(int pos, Supplier<Component> textSupplier, Consumer<Button> clickEvent, Supplier<Boolean> enableDelegate) {
        return addButtonClickable(centerX + 10, getOptionPosition(pos, false), textSupplier, clickEvent, enableDelegate);
    }

    protected Button addButtonClickable(int x, int y, Supplier<Component> textSupplier, Consumer<Button> clickEvent, Supplier<Boolean> enableDelegate) {
        if (enableDelegate == null) enableDelegate = () -> true;
        if (textSupplier == null) textSupplier = () -> Component.literal("");

        var button = Button.builder(textSupplier.get(), btn -> {
            if (clickEvent != null)
                clickEvent.accept(btn);
            pollDelegates();
        })
            .pos(x, y)
            .size(100, 20)
            .build();
        addRenderableWidget(button);
        var delegate = new ButtonDelegate(31 * (31 * 17 + x) + y, enableDelegate, button, null, textSupplier::get);
        delegateMap.put(delegate.id, delegate);
        return button;
    }

    protected Button addButtonConfigToggle(int pos, ConfigSpec.Accessor<Boolean> option) {
        return addButtonConfigToggle(pos, option, null);
    }

    protected Button addButtonConfigToggle(int pos, ConfigSpec.Accessor<Boolean> option, Supplier<Boolean> enableDelegate) {
        if (enableDelegate == null) enableDelegate = () -> true;
        var button = Button.builder(getMessageFromState(option.get()), btn -> {
            option.set(!option.get());
            pollDelegates();
        })
            .pos(centerX + 10, getOptionPosition(pos, false))
            .size(100, 20)
            .build();
        addRenderableWidget(button);
        var delegate = new ButtonDelegate(pos, enableDelegate, button, option, () -> getMessageFromState(option.get()));
        delegateMap.put(delegate.id, delegate);
        return button;
    }

    protected Button addButtonConfigColor(int pos, ConfigSpec.Accessor<ChatFormatting> option, Supplier<Boolean> enableDelegate) {
        var button = Button.builder(getMessageFromColor(option.get()), btn -> {
            var color = option.get();
            int id = color.getId();
            id = (id + 1) % 16;
            option.set(ChatFormatting.getById(id));
            pollDelegates();
        })
            .pos(centerX + 10, getOptionPosition(pos, false))
            .size(100, 20)
            .build();
        addRenderableWidget(button);
        var delegate = new ButtonDelegate(pos, enableDelegate, button, option, () -> getMessageFromColor(option.get()));
        delegateMap.put(delegate.id, delegate);
        return button;
    }

    protected Component getMessageFromColor(ChatFormatting formatting) {
        char[] data = formatting.getName().toCharArray();
        for (int i = 0; i < data.length; i++) {
            if (data[i] == '_') {
                data[i] = ' ';
            } else if (!(i == 0 || data[i - 1] == ' ')) {
                data[i] = Character.toLowerCase(data[i]);
            }
        }
        return Component.literal(String.format("%s%s", formatting, String.valueOf(data)));
    }

    private Component getMessageFromState(boolean state) {
        return Component.translatable(state ? "gui.yes" : "gui.no").setStyle(Style.EMPTY.withColor(state ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_RED));
    }

    private int getOptionPosition(int pos, boolean isText) {
        return getOptionPosition(pos, false, isText);
    }

    private int getOptionPosition(int pos, boolean offset, boolean isText) {
        return centerY / 2 + 24 * (pos - getPageStart()) + (isText ? 6 : 0) + (offset ? Minecraft.getInstance().font.lineHeight : 0);
    }

    @Override
    public void onClose() {
        if (previousMenu != null) {
            Minecraft.getInstance().setScreen(previousMenu);
        } else {
            super.onClose();
        }
    }
}
