package com.kirdow.mentioned.mixin;

import com.kirdow.mentioned.Logger;
import com.kirdow.mentioned.Mentioned;
import com.kirdow.mentioned.PingSound;
import com.kirdow.mentioned.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {

    @ModifyVariable(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V", at = @At("HEAD"), ordinal = 0)
    private Component modifyAddMessageText(Component text) {
        if (Mentioned.skips()) {
            return text;
        }

        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player == null) {
            Logger.info("No player found");
            return text;
        }

        String rawText = text.getString().toLowerCase();
        String playerName = player.getName().getString();

        List<String> names = new ArrayList<>();
        names.addAll(ModConfig.FILTERS);
        if (ModConfig.FILTER_SELF)
            names.add(playerName);

        if (names.stream().map(p -> p.toLowerCase()).anyMatch(p -> rawText.contains(p))) {
            Style style = text.getStyle();
            if (ModConfig.STYLE_COLOR) style = style.withColor(ModConfig.COLOR);
            if (ModConfig.STYLE_BOLD) style = style.withBold(true);
            if (ModConfig.STYLE_ITALIC) style = style.withItalic(true);
            if (ModConfig.STYLE_STRIKETHROUGH) style = style.withStrikethrough(true);
            if (ModConfig.STYLE_UNDERLINE) style = style.withUnderlined(true);
            if (text instanceof MutableComponent mutableText) {
                mutableText.setStyle(style);
            }
            PingSound.playPingSound();
            if (ModConfig.DELAY > 0) {
                Mentioned.skip(ModConfig.DELAY);
            }
        }

        return text;
    }
}
