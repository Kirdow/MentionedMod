package com.kirdow.mentioned.mixin;

import com.kirdow.mentioned.Logger;
import com.kirdow.mentioned.Mentioned;
import com.kirdow.mentioned.PingSound;
import com.kirdow.mentioned.config.ModConfig;
import com.kirdow.mentioned.config.ModOptions;
import com.kirdow.mentioned.events.ChatEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
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

    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/client/gui/hud/ChatHud;addVisibleMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V", ordinal = 0))
    private ChatHudLine modifyAddMessageText(ChatHudLine hudLine) {
        if (Mentioned.skips()) {
            return hudLine;
        }

        if (hudLine.content() instanceof MutableText mutableText) {
            return new ChatHudLine(hudLine.creationTick(), ChatEvents.applyFor(mutableText), hudLine.signature(), hudLine.indicator());
        }

        return hudLine;
    }
}
