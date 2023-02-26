package com.vandendaelen.nicephore.client.gui;

import com.vandendaelen.nicephore.Nicephore;
import com.vandendaelen.nicephore.helper.PlayerHelper;
import com.vandendaelen.nicephore.util.CopyImageToClipBoard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DeleteConfirmScreen extends Screen {

    private final File file;
    private final int screenshotScreenIndex;
    private final int galleryScreenIndex;

    protected DeleteConfirmScreen(File file, int screenshotScreenIndex, int galleryScreenIndex) {
        super(Text.translatable("nicephore.gui.delete"));
        this.file = file;
        this.screenshotScreenIndex = screenshotScreenIndex;
        this.galleryScreenIndex = galleryScreenIndex;
    }

    protected DeleteConfirmScreen(File file, int screenshotScreenIndex) {
        this(file, screenshotScreenIndex, -1);
    }

    @Override
    protected void init() {
        super.init();

        var confirmButton = ButtonWidget.builder(Text.translatable("nicephore.gui.delete.yes"), button -> {
                    deleteScreenshot();
                    MinecraftClient.getInstance().setScreen(new ScreenshotScreen(screenshotScreenIndex, galleryScreenIndex));
                }).dimensions(this.width / 2 - 35, this.height / 2 + 30, 30, 20)
                .build();
        var denyButton = ButtonWidget.builder(Text.translatable("nicephore.gui.delete.no"), button -> MinecraftClient.getInstance().setScreen(new ScreenshotScreen()))
                .dimensions(this.width / 2 + 5, this.height / 2 + 30, 30, 20)
                .build();

        this.children().clear();
        this.addDrawableChild(confirmButton);
        this.addDrawableChild(denyButton);

    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        drawCenteredText(matrixStack, MinecraftClient.getInstance().textRenderer, Text.translatable("nicephore.gui.delete.question", file.getName()).getString(), this.width / 2, this.height / 2 - 20, Color.RED.getRGB());
    }

    private void deleteScreenshot() {
        if (CopyImageToClipBoard.getInstance().isLastScreenshot(this.file)) {
            CopyImageToClipBoard.getInstance().setLastScreenshot(null);
        }

        try {
            if (Files.deleteIfExists(file.toPath())) {
                PlayerHelper.sendMessage(Text.translatable("nicephore.screenshot.deleted.success", file.getName()));
            } else {
                PlayerHelper.sendMessage(Text.translatable("nicephore.screenshot.deleted.error", file.getName()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            PlayerHelper.sendMessage(Text.translatable("nicephore.screenshot.deleted.error", file.getName()));
        }

    }
}
