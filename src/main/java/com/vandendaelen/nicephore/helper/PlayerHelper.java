package com.vandendaelen.nicephore.helper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;

public class PlayerHelper {
    public static void sendHotbarMessage(TranslatableText translatableText) {
        MinecraftClient.getInstance().player.sendMessage(translatableText, true);
    }

    public static void sendMessage(TranslatableText translatableText) {
        MinecraftClient.getInstance().player.sendMessage(translatableText, false);
    }
}
