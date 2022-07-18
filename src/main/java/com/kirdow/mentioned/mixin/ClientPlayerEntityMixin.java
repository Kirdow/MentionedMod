package com.kirdow.mentioned.mixin;

import com.kirdow.mentioned.Mentioned;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "sendChatMessagePacket(Lnet/minecraft/network/message/ChatMessageSigner;Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"))
    private void chatMessagePacketHook(CallbackInfo ci) {
        Mentioned.skip(1000L);
    }
}
