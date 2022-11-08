package com.kirdow.mentioned;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class PingSound {

    public static void playPingSound() {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        ClientLevel world = client.level;

        if (player == null || world == null) return;

        world.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.MASTER, 0.5f, 1.0f, false);
    }

}
