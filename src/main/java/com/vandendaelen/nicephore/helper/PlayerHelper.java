package com.vandendaelen.nicephore.helper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;

public class PlayerHelper {
    public static void sendHotbarMessage(MutableText translatableText) {
        if (MinecraftClient.getInstance().player != null)
        MinecraftClient.getInstance().player.sendMessage(translatableText, true);
    }

    public static void sendMessage(MutableText translatableText) {
        if (MinecraftClient.getInstance().player != null)
        MinecraftClient.getInstance().player.sendMessage(translatableText, false);
    }
}
