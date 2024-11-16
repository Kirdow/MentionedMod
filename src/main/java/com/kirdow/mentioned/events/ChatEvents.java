package com.kirdow.mentioned.events;

import com.kirdow.mentioned.Logger;
import com.kirdow.mentioned.Mentioned;
import com.kirdow.mentioned.PingSound;
import com.kirdow.mentioned.config.ConfigManager;
import com.kirdow.mentioned.util.Either;
import com.kirdow.mentioned.util.Ref;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChatEvents {

    private MutableText text;
    private boolean ping = false;
    private List<String> names = new ArrayList<>();
    private Pattern regex;

    private ChatEvents(MutableText text) {
        this.text = text;
        names.addAll(ConfigManager.get().filter);
        if (ConfigManager.get().filterSelf)
            names.add(MinecraftClient.getInstance().player.getName().getString());
        regex = Pattern.compile(String.format("(%s)", String.join("|", names)), Pattern.CASE_INSENSITIVE);
    }

    private ChatEvents(MutableText text, ChatEvents parent) {
        this.text = text;
        this.names.addAll(parent.names);
        this.regex = parent.regex;
    }

    public static MutableText applyFor(MutableText text) {
        ChatEvents events = new ChatEvents(text);
        events.run();
        if (events.ping) {
            PingSound.playPingSound();
            long delay = ConfigManager.get().delay;
            if (delay > 0) {
                Mentioned.skip(delay);
            }
        }

        return events.text;
    }

    private MutableText applyNext(MutableText text, ChatEvents parent) {
        ChatEvents events = new ChatEvents(text, parent);
        events.run();
        this.ping |= events.ping;
        return events.text;
    }

    public void run() {
        MutableText result;
        if (text.getContent() instanceof PlainTextContent content) {
            String stringContent = content.string();

            result = apply(stringContent, text);
        } else if (text.getContent() instanceof TranslatableTextContent content) {
            result = Text.translatable(content.getKey(), Arrays.stream(content.getArgs()).map(p -> {
                if (p instanceof MutableText mutableText) {
                    return applyNext(mutableText, this);
                } else if (p instanceof String str) {
                    return apply(str, Text.empty());
                } else {
                    return p;
                }
            }).collect(Collectors.toList()).toArray(new Object[0])).setStyle(text.getStyle());
        } else {
            result = text.copy();
            result.getSiblings().clear();
        }

        List<Text> siblings = text.getSiblings();
        for (Text sibling : siblings) {
            if (sibling instanceof MutableText mutableSibling) {
                result.append(applyNext(mutableSibling, this));
            } else {
                result.append(sibling);
            }
        }

        text = result;
    }

    private MutableText apply(String str, MutableText mutableText) {
        MutableText result = null;
        List<Either<String, Pair<String, Style>>> replacement = replaceNames(str, mutableText.getStyle());
        for (Either<String, Pair<String, Style>> segment : replacement) {
            MutableText nextText = null;
            Ref<String> outNormal = new Ref<>(null);
            Ref<Pair<String, Style>> outName = new Ref<>(null);

            if (segment.isLeft(outNormal)) {
                nextText = Text.literal(outNormal.value).setStyle(text.getStyle());
            } else if (segment.isRight(outName)) {
                String names = String.format("Ping keywords: %s", String.join(", ", this.names));

                Style style = outName.value.getRight();
                if (!names.isEmpty()) {
                    style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("\u00A77" + names)));
                }

                nextText = Text.literal(outName.value.getLeft()).setStyle(style);
            }

            if (result == null) {
                if (segment.isRight(null)) {
                    result = Text.literal("").setStyle(Style.EMPTY);
                    result.getSiblings().add(nextText);
                } else {
                    result = nextText;
                }
            } else {
                result.getSiblings().add(nextText);
            }
        }

        return result == null ? Text.empty() : result;
    }

    private Style getStyle(Style style) {
        if (ConfigManager.get().styleColor) style = style.withColor(ConfigManager.get().color);
        if (ConfigManager.get().styleBold) style = style.withBold(true);
        if (ConfigManager.get().styleItalic) style = style.withItalic(true);
        if (ConfigManager.get().styleStrikethrough) style = style.withStrikethrough(true);
        if (ConfigManager.get().styleUnderline) style = style.withUnderline(true);

        return style;
    }

    private List<Either<String, Pair<String, Style>>> replaceNames(String str, Style style) {
        List<Either<String, Pair<String, Style>>> result = new ArrayList<>();

        Matcher matcher = regex.matcher(str);
        int lastEnd = 0;

        while (matcher.find()) {
            if (lastEnd < matcher.start()) {
                String betweenMatches = str.substring(lastEnd, matcher.start());
                result.add(Either.ofLeft(betweenMatches));
            }

            String name = matcher.group();
            result.add(Either.ofRight(Pair.of(name, getStyle(style))));

            ping = true;

            lastEnd = matcher.end();
        }

        if (lastEnd < str.length()) {
            String tail = str.substring(lastEnd);
            result.add(Either.ofLeft(tail));
        }

        return result;
    }

}
