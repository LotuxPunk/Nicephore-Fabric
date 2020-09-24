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
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
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

        if (screenshots.isEmpty()){
            this.onClose();
            return;
        }

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
        this.addButton(new ButtonWidget(this.width / 2 - 55, this.height / 2 + 75, 50, 20, new TranslatableText("nicephore.gui.screenshots.copy"), button -> {
            final CopyImageToClipBoard imageToClipBoard = new CopyImageToClipBoard();
            try {
                imageToClipBoard.copyImage(ImageIO.read(screenshots.get(index)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 5, this.height / 2 + 75, 50, 20,new TranslatableText("nicephore.gui.screenshots.delete"), button -> deleteScreenshot(screenshots.get(index))));
    }

    private void deleteScreenshot(File file) {
        MinecraftClient.getInstance().openScreen(new DeleteConfirmScreen(file));
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

        drawCenteredText(matrixStack, MinecraftClient.getInstance().textRenderer, new TranslatableText("nicephore.gui.screenshots.pages", index + 1, screenshots.size()), centerX, 20, Color.WHITE.getRGB());
        drawCenteredText(matrixStack, MinecraftClient.getInstance().textRenderer, new LiteralText(MessageFormat.format("{0} ({1})", screenshots.get(index).getName(), getFileSizeMegaBytes(screenshots.get(index)))), centerX, 35, Color.WHITE.getRGB());
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
        if (index >= screenshots.size() || index < 0){
            index = screenshots.size() - 1;
        }
        return index;
    }

    public static boolean canBeShow(){
        return SCREENSHOTS_DIR.list().length > 0;
    }

    private static String getFileSizeMegaBytes(File file) {
        final double size = FileUtils.sizeOf(file);
        final NumberFormat formatter = new DecimalFormat("#0.00");
        final int MB_SIZE = 1024 * 1024;
        final int KB_SIZE = 1024;

        if (size > MB_SIZE){
            return MessageFormat.format("{0} MB", formatter.format((double) FileUtils.sizeOf(file) / MB_SIZE));
        }
        return MessageFormat.format("{0} KB", formatter.format((double) FileUtils.sizeOf(file) / KB_SIZE));
    }
}
