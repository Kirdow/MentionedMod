package com.kirdow.mentioned.mixin;

import com.kirdow.mentioned.Mentioned;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketMixin {

    @Inject(method = "sendChat(Ljava/lang/String;)V", at = @At("HEAD"))
    private void chatMessagePacketHook(CallbackInfo ci) {
        Mentioned.skip(1000L);
    }
}
