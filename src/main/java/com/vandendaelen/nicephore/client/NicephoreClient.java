package com.vandendaelen.nicephore.client;

import com.vandendaelen.nicephore.helper.PlayerHelper;
import com.vandendaelen.nicephore.thread.InitThread;
import com.vandendaelen.nicephore.util.CopyImageToClipBoard;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

@Environment(EnvType.CLIENT)
public class NicephoreClient implements ClientModInitializer {
    private static KeyBinding copyKeyBinding;
    private static KeyBinding guiScreenshotKeyBinding;
    private static KeyBinding guiGalleryKeyBinding;

    @Override
    public void onInitializeClient() {
        System.setProperty("java.awt.headless", "false");

        copyKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "nicephore.keybinds.copy",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "nicephore.keybinds.category"
        ));
        guiScreenshotKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "nicephore.keybinds.screenshots.gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_S,
                "nicephore.keybinds.category"
        ));
        guiGalleryKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "nicephore.keybinds.gallery.gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "nicephore.keybinds.category"
        ));

        ClientLifecycleEvents.CLIENT_STARTED.register(minecraftClient -> {
            final InitThread initThread = new InitThread();
            initThread.start();
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (copyKeyBinding.wasPressed()) {
                final CopyImageToClipBoard imageToClipBoard = new CopyImageToClipBoard();
                try {
                    imageToClipBoard.copyLastScreenshot();
                    PlayerHelper.sendMessage(new TranslatableText("nicephore.clipboard.success"));
                } catch (IOException e) {
                    PlayerHelper.sendMessage(new TranslatableText("nicephore.clipboard.error"));
                    e.printStackTrace();
                }
            }

            if (guiScreenshotKeyBinding.wasPressed()){
                if (ScreenshotScreen.canBeShow()){
                    MinecraftClient.getInstance().setScreen(new ScreenshotScreen());
                }
                else {
                    PlayerHelper.sendHotbarMessage(new TranslatableText("nicephore.screenshots.empty"));
                }
            }

            if (guiGalleryKeyBinding.wasPressed()){
                if (GalleryScreen.canBeShow()){
                    MinecraftClient.getInstance().setScreen(new GalleryScreen());
                }
                else {
                    PlayerHelper.sendHotbarMessage(new TranslatableText("nicephore.screenshots.empty"));
                }
            }
        });
    }
}
