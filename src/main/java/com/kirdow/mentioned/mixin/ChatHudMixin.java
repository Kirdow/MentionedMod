package com.kirdow.mentioned.mixin;

import com.kirdow.mentioned.Mentioned;
import com.kirdow.mentioned.PingSound;
import com.kirdow.mentioned.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ChatHud.class)
public class ChatHudMixin {

    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), ordinal = 0)
    private Text modifyAddMessageText(Text text) {
        if (Mentioned.skips()) {
            return text;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) {
            Mentioned.LOGGER.info("No player found");
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
            if (ModConfig.STYLE_UNDERLINE) style = style.withUnderline(true);
            if (text instanceof MutableText mutableText) {
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
