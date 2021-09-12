package com.vandendaelen.nicephore.client;

import com.vandendaelen.nicephore.helper.PlayerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import java.awt.*;
import java.io.File;

public class DeleteConfirmScreen extends Screen {

    private final File file;

    protected DeleteConfirmScreen(File file) {
        super(new TranslatableText("nicephore.gui.delete"));
        this.file = file;
    }

    @Override
    protected void init() {
        super.init();

        this.children().clear();
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 35, this.height / 2 + 30, 30, 20, new TranslatableText("nicephore.gui.delete.yes"), button -> {
            deleteScreenshot();
            MinecraftClient.getInstance().setScreen(new ScreenshotScreen());
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, this.height / 2 + 30, 30, 20, new TranslatableText("nicephore.gui.delete.no"), button -> MinecraftClient.getInstance().setScreen(new ScreenshotScreen())));

    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        drawCenteredText(matrixStack, MinecraftClient.getInstance().textRenderer, new TranslatableText("nicephore.gui.delete.question", file.getName()).getString(), this.width / 2, this.height / 2 - 20, Color.RED.getRGB());
    }

    private void deleteScreenshot(){
        if (this.file.exists() && this.file.delete()){
            PlayerHelper.sendMessage(new TranslatableText("nicephore.screenshot.deleted.success", file.getName()));
        }
        else{
            PlayerHelper.sendMessage(new TranslatableText("nicephore.screenshot.deleted.error", file.getName()));
        }
    }
}
