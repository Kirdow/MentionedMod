package com.kirdow.mentioned.mixin;

import com.kirdow.mentioned.Mentioned;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayerNetworkMixin {

    @Inject(method = "sendChatMessage(Ljava/lang/String;)V", at = @At("HEAD"))
    private void chatMessagePacketHook(CallbackInfo ci) {
        Mentioned.skip(1000L);
    }
}
