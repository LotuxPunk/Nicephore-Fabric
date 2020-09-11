package com.vandendaelen.nicephore.client;

import com.vandendaelen.nicephore.util.CopyImageToClipBoard;
import com.vandendaelen.nicephore.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ScreenshotScreen extends Screen {
    private static final TranslatableText TITLE = new TranslatableText("nicephore.gui.screenshots");
    private static final File SCREENSHOTS_DIR = new File(MinecraftClient.getInstance().runDirectory, "screenshots");
    private static final TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
    private static Identifier SCREENSHOT_TEXTURE;
    private ArrayList<File> screenshots;
    private static int index;
    private float aspectRatio;

    public ScreenshotScreen() {
        super(TITLE);

        FilenameFilter filter = (dir, name) -> name.endsWith(".jpg") || name.endsWith(".png");

        screenshots = (ArrayList<File>) Arrays.stream(SCREENSHOTS_DIR.listFiles(filter)).collect(Collectors.toList());
        index = getIndex();
        aspectRatio = 1.7777F;
    }


    @Override
    protected void init() {
        super.init();

        BufferedImage bimg = null;
        try {
            bimg = ImageIO.read(screenshots.get(index));
            int width = bimg.getWidth();
            int height = bimg.getHeight();
            bimg.getGraphics().dispose();
            aspectRatio = (float)(width/(double)height);
        } catch (IOException e) {
            e.printStackTrace();
        }

        textureManager.destroyTexture(SCREENSHOT_TEXTURE);
        SCREENSHOT_TEXTURE = Util.fileTotexture(screenshots.get(index));

        this.buttons.clear();
        this.addButton(new ButtonWidget(this.width / 2 + 50, this.height / 2 + 75, 20, 20, new LiteralText(">"), button -> modIndex(1)));
        this.addButton(new ButtonWidget(this.width / 2 - 80, this.height / 2 + 75, 20, 20, new LiteralText("<"), button -> modIndex(-1)));
        this.addButton(new ButtonWidget(this.width / 2 - 30, this.height / 2 + 75, 50, 20, new TranslatableText("nicephore.gui.screenshots.copy"), button -> {
            final CopyImageToClipBoard imageToClipBoard = new CopyImageToClipBoard();
            try {
                imageToClipBoard.copyImage(ImageIO.read(screenshots.get(index)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        textureManager.bindTexture(SCREENSHOT_TEXTURE);

        int centerX = this.width / 2;
        int width = (int) (this.width * 0.5);
        int height = (int)(width / aspectRatio);
        drawTexture(matrixStack, centerX - width / 2, 50, 0, 0, width, height, width, height);

        drawCenteredText(matrixStack, MinecraftClient.getInstance().textRenderer, new TranslatableText("nicephore.gui.screenshots.pages", index + 1, screenshots.size()), this.width / 2, 30, Color.WHITE.getRGB());
        drawCenteredText(matrixStack, MinecraftClient.getInstance().textRenderer, new TranslatableText("nicephore.gui.screenshots"), this.width / 2, 20, Color.WHITE.getRGB());
        drawCenteredText(matrixStack, MinecraftClient.getInstance().textRenderer, new LiteralText(screenshots.get(index).getName()), this.width / 2, (int) (this.height * 0.9), Color.WHITE.getRGB());
    }

    private void modIndex(int value){
        int max = screenshots.size();
        if (index + value >= 0 && index + value < max){
            index += value;
        }
        else {
            if (index + value < 0){
                index = max - 1;
            }
            else {
                index = 0;
            }
        }
        init();
    }

    private int getIndex(){
        if (index >= screenshots.size()){
            index = screenshots.size() - 1;
        }
        return index;
    }

    public static boolean canBeShow(){
        return SCREENSHOTS_DIR.list().length > 0;
    }
}
