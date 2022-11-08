package com.kirdow.mentioned.mixin;

import com.kirdow.mentioned.Logger;
import com.kirdow.mentioned.Mentioned;
import com.kirdow.mentioned.PingSound;
import com.kirdow.mentioned.config.ModConfig;
import com.kirdow.mentioned.config.ModOptions;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
            Logger.info("No player found");
            return text;
        }
        String rawText = text.getString().toLowerCase();

        String playerName = player.getName().getString();

        List<String> names = new ArrayList<>();
        names.addAll(Arrays.stream(ModOptions.filtersValue.get()).collect(Collectors.toList()));
        if (ModOptions.filterSelfValue.get())
            names.add(playerName);

        if (names.stream().map(p -> p.toLowerCase()).anyMatch(p -> rawText.contains(p))) {
            Style style = text.getStyle();
            if (ModOptions.useColorValue.get()) style = style.withColor(ModOptions.colorValue.get());
            if (ModOptions.useBoldValue.get()) style = style.withBold(true);
            if (ModOptions.useItalicValue.get()) style = style.withItalic(true);
            if (ModOptions.useStrikeThroughValue.get()) style = style.withStrikethrough(true);
            if (ModOptions.useUnderlineValue.get()) style = style.withUnderline(true);
            if (text instanceof MutableText mutableText) {
                mutableText.setStyle(style);
            }
            PingSound.playPingSound();
            int delay = ModOptions.delayValue.get();
            if (delay > 0) {
                Mentioned.skip(delay);
            }
        }

        return text;
    }
}
