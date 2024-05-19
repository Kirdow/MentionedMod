package com.kirdow.mentioned.config.gui;

import com.kirdow.mentioned.config.ConfigSpec;
import com.kirdow.mentioned.config.ModOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.EditBox;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MentionedConfigMenu extends Screen {

    private class ButtonDelegate<T> {
        int id;
        ButtonWidget button;
        ConfigSpec.Accessor<T> option;
        Supplier<Boolean> delegate;
        Supplier<Text> messageDelegate;

        public ButtonDelegate(int id, Supplier<Boolean> delegate, ButtonWidget button, ConfigSpec.Accessor<T> option, Supplier<Text> messageDelegate) {
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
    private TextFieldWidget filtersEditBox;

    private Text activeTooltip;

    private Map<Integer, ButtonDelegate> delegateMap = new HashMap<>();

    public MentionedConfigMenu() {
        this(null);
    }

    public MentionedConfigMenu(Screen previous) {
        super(Text.translatable("config.ktnmentioned.base.config_title"));
        previousMenu = previous;
    }

    private void pollDelegates() {
        for (var entry : delegateMap.entrySet()) {
            var value = entry.getValue();
            value.button.active = (Boolean)value.delegate.get();
        }

        for (var entry : delegateMap.entrySet()) {
            var value = entry.getValue();
            value.button.setMessage(value.button.active ? (Text)value.messageDelegate.get() : Text.literal(""));
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
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        activeTooltip = null;
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        renderBackground(ms);
        var titleText = Text.translatable("config.ktnmentioned.base.config_title");
        var font = MinecraftClient.getInstance().textRenderer;
        font.drawWithShadow(ms, titleText, (width - font.getWidth(titleText.getString())) / 2, 20, 0xf0f0f0);

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
            var hoverTextLines = font.getTextHandler().wrapLines(activeTooltip, 134, Style.EMPTY);
            List<Text> textLines = new ArrayList<>();
            for (var textLine : hoverTextLines) {
                textLine.visit((style, str) -> Optional.of((Text)(Text.literal(str)).setStyle(style)), Style.EMPTY)
                        .ifPresent(textLines::add);
            }
            renderTooltip(ms, textLines, mouseX, mouseY);
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
        addButtonClickable(10, 10, () -> Text.translatable("gui.back"), btn -> close(), null);

        generatePageEntry(2, pos -> addButtonClickable(2, () -> Text.translatable("config.ktnmentioned.update"), btn -> {
            Set<String> filters = Arrays.stream(filtersEditBox.getText().split(";")).collect(Collectors.toSet());
            ModOptions.filtersValue.set(filters.toArray(new String[0]));
            MentionedConfigMenu.this.filters = null;
        }, null));

        generatePageEntry(2, pos -> {
            filtersEditBox = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, centerX - 200, getOptionPosition(2, false), 200, 20, null);
            addDrawableChild(filtersEditBox);
            filtersEditBox.setText(String.join(";", ModOptions.filtersValue.get()));
        });

        generatePageEntry(3, pos -> addButtonConfigToggle(3, ModOptions.filterSelfValue));
        generatePageEntry(5, pos -> addButtonConfigToggle(5, ModOptions.useColorValue));
        generatePageEntry(6, pos -> addButtonConfigColor(6, ModOptions.colorValue, () -> ModOptions.useColorValue.get()));
        generatePageEntry(7, pos -> addButtonConfigToggle(7, ModOptions.useBoldValue));
        generatePageEntry(8, pos -> addButtonConfigToggle(8, ModOptions.useItalicValue));
        generatePageEntry(9, pos -> addButtonConfigToggle(9, ModOptions.useStrikeThroughValue));
        generatePageEntry(10, pos -> addButtonConfigToggle(10, ModOptions.useUnderlineValue));

        addButtonClickable(Math.max(10, centerX - 400), height - 30, () -> Text.translatable("config.ktnmentioned.prev"), btn -> {
            if (checkPage(-1)) --currentPage;
            reloadPage();
        }, () -> checkPage(-1));
        addButtonClickable(Math.min(width - 110, centerX + 300), height - 30, () -> Text.translatable("config.ktnmentioned.next"), btn -> {
            if (checkPage(1)) ++currentPage;
            reloadPage();
        }, () -> checkPage(1));

        pollDelegates();
    }

    private void reloadPage() {
        filtersEditBox = null;
        clearChildren();
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

    protected void drawButtonPrefix(MatrixStack ms, int pos, Object key, Object hover) {
        MutableText keyComp = null;
        if (key instanceof String keyString) {
            keyComp = Text.translatable(keyString);
        } else if (key instanceof MutableText mcomp) {
            keyComp = mcomp;
        } else if (key != null) {
            keyComp = Text.literal(key.toString());
        } else {
            return;
        }

        MutableText hoverComp = null;
        if (hover instanceof String hoverString) {
            hoverComp = Text.translatable(hoverString);
        } else if (hover instanceof MutableText mcomp) {
            hoverComp = mcomp;
        } else if (hover != null) {
            hoverComp = Text.literal(hover.toString());
        }

        var style = Style.EMPTY.withColor(Formatting.WHITE);
        if (hoverComp != null) {
            style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComp.setStyle(Style.EMPTY.withColor(Formatting.GREEN))));
        }
        drawButtonPrefix(ms, keyComp.setStyle(style), pos, hoverComp == null);
    }

    protected void drawButtonPrefix(MatrixStack ms, Text text, int pos, boolean isTitle) {
        var font = MinecraftClient.getInstance().textRenderer;

        int textWidth = font.getWidth(text.getString());
        int x = centerX - (isTitle ? (textWidth / 2) : (textWidth + 10));
        int y = getOptionPosition(pos, true);

        font.drawWithShadow(ms, text, x, y, 0xf0f0f0);

        if (!(mouseX >= x - 10 && mouseY >= y - 10 && mouseX <= x + textWidth + 10 && mouseY <= y + font.fontHeight + 10))
            return;

        HoverEvent event = text.getStyle().getHoverEvent();
        if (event != null && event.getAction() == HoverEvent.Action.SHOW_TEXT) {
            activeTooltip = event.getValue(HoverEvent.Action.SHOW_TEXT);
        }

    }

    protected ButtonWidget addButtonClickable(int pos, Supplier<Text> textSupplier, Consumer<ButtonWidget> clickEvent, Supplier<Boolean> enableDelegate) {
        return addButtonClickable(centerX + 10, getOptionPosition(pos, false), textSupplier, clickEvent, enableDelegate);
    }

    protected ButtonWidget addButtonClickable(int x, int y, Supplier<Text> textSupplier, Consumer<ButtonWidget> clickEvent, Supplier<Boolean> enableDelegate) {
        if (enableDelegate == null) enableDelegate = () -> true;
        if (textSupplier == null) textSupplier = () -> Text.literal("");

        var button = ButtonWidget.builder(textSupplier.get(), btn -> {
            if (clickEvent != null)
                clickEvent.accept(btn);
            pollDelegates();
        })
            .position(x, y)
            .size(100, 20)
            .build();
        addDrawableChild(button);
        var delegate = new ButtonDelegate(31 * (31 * 17 + x) + y, enableDelegate, button, null, textSupplier::get);
        delegateMap.put(delegate.id, delegate);
        return button;
    }

    protected ButtonWidget addButtonConfigToggle(int pos, ConfigSpec.Accessor<Boolean> option) {
        return addButtonConfigToggle(pos, option, null);
    }

    protected ButtonWidget addButtonConfigToggle(int pos, ConfigSpec.Accessor<Boolean> option, Supplier<Boolean> enableDelegate) {
        if (enableDelegate == null) enableDelegate = () -> true;
        var button = ButtonWidget.builder(getMessageFromState(option.get()), btn -> {
            option.set(!option.get());
            pollDelegates();
        })
            .position(centerX + 10, getOptionPosition(pos, false))
            .size(100, 20)
            .build();
        addDrawableChild(button);
        var delegate = new ButtonDelegate(pos, enableDelegate, button, option, () -> getMessageFromState(option.get()));
        delegateMap.put(delegate.id, delegate);
        return button;
    }

    protected ButtonWidget addButtonConfigColor(int pos, ConfigSpec.Accessor<Formatting> option, Supplier<Boolean> enableDelegate) {
        var button = ButtonWidget.builder(getMessageFromColor(option.get()), btn -> {
            var color = option.get();
            int id = color.getColorIndex();
            id = (id + 1) % 16;
            option.set(Formatting.byColorIndex(id));
            pollDelegates();
        })
            .position(centerX + 10, getOptionPosition(pos, false))
            .size(100, 20)
            .build();
        addDrawableChild(button);
        var delegate = new ButtonDelegate(pos, enableDelegate, button, option, () -> getMessageFromColor(option.get()));
        delegateMap.put(delegate.id, delegate);
        return button;
    }

    protected Text getMessageFromColor(Formatting formatting) {
        char[] data = formatting.getName().toCharArray();
        for (int i = 0; i < data.length; i++) {
            if (data[i] == '_') {
                data[i] = ' ';
            } else if (!(i == 0 || data[i - 1] == ' ')) {
                data[i] = Character.toLowerCase(data[i]);
            }
        }
        return Text.literal(String.format("%s%s", formatting, String.valueOf(data)));
    }

    private Text getMessageFromState(boolean state) {
        return Text.translatable(state ? "gui.yes" : "gui.no").setStyle(Style.EMPTY.withColor(state ? Formatting.DARK_GREEN : Formatting.DARK_RED));
    }

    private int getOptionPosition(int pos, boolean isText) {
        return getOptionPosition(pos, false, isText);
    }

    private int getOptionPosition(int pos, boolean offset, boolean isText) {
        return centerY / 2 + 24 * (pos - getPageStart()) + (isText ? 6 : 0) + (offset ? MinecraftClient.getInstance().textRenderer.fontHeight : 0);
    }

    @Override
    public void close() {
        if (previousMenu != null) {
            MinecraftClient.getInstance().setScreen(previousMenu);
        } else {
            super.close();
        }
    }

}
