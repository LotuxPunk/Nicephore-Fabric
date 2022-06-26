package com.vandendaelen.nicephore.client.gui;

import com.vandendaelen.nicephore.helper.PlayerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;
import java.io.File;

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

        this.children().clear();
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 35, this.height / 2 + 30, 30, 20, Text.translatable("nicephore.gui.delete.yes"), button -> {
            deleteScreenshot();
            MinecraftClient.getInstance().setScreen(new ScreenshotScreen(screenshotScreenIndex, galleryScreenIndex));
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, this.height / 2 + 30, 30, 20, Text.translatable("nicephore.gui.delete.no"), button -> MinecraftClient.getInstance().setScreen(new ScreenshotScreen())));

    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        drawCenteredText(matrixStack, MinecraftClient.getInstance().textRenderer, Text.translatable("nicephore.gui.delete.question", file.getName()).getString(), this.width / 2, this.height / 2 - 20, Color.RED.getRGB());
    }

    private void deleteScreenshot() {
        if (this.file.exists() && this.file.delete()) {
            PlayerHelper.sendMessage(Text.translatable("nicephore.screenshot.deleted.success", file.getName()));
        } else {
            PlayerHelper.sendMessage(Text.translatable("nicephore.screenshot.deleted.error", file.getName()));
        }
    }
}
