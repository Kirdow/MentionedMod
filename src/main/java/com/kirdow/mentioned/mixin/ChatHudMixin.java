package com.kirdow.mentioned.mixin;

import com.kirdow.mentioned.Mentioned;
import com.kirdow.mentioned.PingSound;
import com.kirdow.mentioned.util.Ref;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.BaseText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Mixin(ChatHud.class)
public class ChatHudMixin {

    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), ordinal = 0)
    private Text modifyAddMessageText(Text text) {
        if (Mentioned.skips()) {
            return text;
        }

        Mentioned.LOGGER.info("ADD MESSAGE MOD");
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) {
            Mentioned.LOGGER.info("NO PLAYER");
            return text;
        }
        String rawText = text.getString().toLowerCase();
        Mentioned.LOGGER.info("Raw Text: \"{}\"!", rawText);

        String playerName = player.getName().getString();

        List<String> names = new ArrayList<>();
        names.add(playerName);

        if (names.stream().map(p -> p.toLowerCase()).anyMatch(p -> rawText.contains(p))) {
            Style style = text.getStyle();
            style = style.withColor(Formatting.GOLD);
            if (text instanceof BaseText baseText) {
                baseText.setStyle(style);
            }
            PingSound.playPingSound();
        }

        return text;
    }
}
