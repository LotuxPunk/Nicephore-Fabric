package com.vandendaelen.nicephore.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.vandendaelen.nicephore.config.NicephoreConfig;
import com.vandendaelen.nicephore.helper.PlayerHelper;
import com.vandendaelen.nicephore.util.ScreenshotFilter;
import com.vandendaelen.nicephore.util.Util;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GalleryScreen extends Screen {
    private static final TranslatableText TITLE = new TranslatableText("nicephore.gui.screenshots");
    private static final File SCREENSHOTS_DIR = new File(MinecraftClient.getInstance().runDirectory, "screenshots");
    private static ArrayList<NativeImageBackedTexture> SCREENSHOT_TEXTURES = new ArrayList<>();
    private ArrayList<File> screenshots;
    private ArrayList<List<File>> pagesOfScreenshots;
    private int index;
    private float aspectRatio;
    private final NicephoreConfig config;

    private static final int ROW = 2;
    private static final int COLUMN = 4;
    private static final int IMAGES_TO_DISPLAY = ROW * COLUMN;

    public GalleryScreen() {
        super(TITLE);
        config = AutoConfig.getConfigHolder(NicephoreConfig.class).getConfig();
    }

    public GalleryScreen(int index) {
        super(TITLE);
        this.index = index;
        config = AutoConfig.getConfigHolder(NicephoreConfig.class).getConfig();
    }

    @Override
    protected void init() {
        super.init();

        screenshots = (ArrayList<File>) Arrays.stream(SCREENSHOTS_DIR.listFiles(config.getFilter().getPredicate())).sorted(Comparator.comparingLong(File::lastModified).reversed()).collect(Collectors.toList());
        pagesOfScreenshots = (ArrayList<List<File>>) Util.batches(screenshots,IMAGES_TO_DISPLAY).collect(Collectors.toList());
        index = getIndex();
        aspectRatio = 1.7777F;

        if (!screenshots.isEmpty()) {
            try {
                BufferedImage bimg = ImageIO.read(screenshots.get(index));
                final int width = bimg.getWidth();
                final int height = bimg.getHeight();
                bimg.getGraphics().dispose();
                aspectRatio = (float) (width / (double) height);
            } catch (IOException e) {
                e.printStackTrace();
            }

            SCREENSHOT_TEXTURES.forEach(NativeImageBackedTexture::close);
            SCREENSHOT_TEXTURES.clear();

            List <File> filesToLoad = pagesOfScreenshots.get(index);
            if (!filesToLoad.isEmpty()) {
                filesToLoad.forEach(file -> SCREENSHOT_TEXTURES.add( Util.fileToTexture(file)));
            } else {
                closeScreen("nicephore.screenshots.loading.error");
                return;
            }
        }
    }
    private void changeFilter(){
        ScreenshotFilter nextFilter = config.getFilter().next();
        config.setFilter(nextFilter);
        init();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        final int centerX = this.width / 2;
        final int imageWidth = (int) (this.width * 1.0/5);
        final int imageHeight = (int) (imageWidth / aspectRatio);

        this.renderBackground(matrixStack);

        this.clearChildren();
        this.addDrawableChild(new ButtonWidget(10, 10, 100, 20, new TranslatableText("nicephore.screenshot.filter", config.getFilter().name()), button -> changeFilter()));
        this.addDrawableChild(new ButtonWidget(this.width - 60, 10, 50, 20, new TranslatableText("nicephore.screenshot.exit"), button -> onClose()));

        if (!screenshots.isEmpty()) {
            this.addDrawableChild(new ButtonWidget(this.width / 2 - 80, this.height / 2 + 100, 20, 20, new LiteralText("<"), button -> modIndex(-1)));
            this.addDrawableChild(new ButtonWidget(this.width / 2 + 60, this.height / 2 + 100, 20, 20, new LiteralText(">"), button -> modIndex(1)));
        }

        if (pagesOfScreenshots.isEmpty()){
            drawCenteredText(matrixStack, MinecraftClient.getInstance().textRenderer, new TranslatableText("nicephore.screenshots.empty"), centerX, 20, Color.RED.getRGB());
            return;
        }

        final List<File> currentPage = pagesOfScreenshots.get(index);
        if (currentPage.stream().allMatch(File::exists)){
            SCREENSHOT_TEXTURES.forEach(TEXTURE -> {
                final int imageIndex = SCREENSHOT_TEXTURES.indexOf(TEXTURE);
                final String name = currentPage.get(imageIndex).getName();
                final LiteralText text = new LiteralText(StringUtils.abbreviate(name, 13));

                int x = centerX - (15 - (imageIndex % 4) * 10) - (2 - (imageIndex % 4)) * imageWidth;
                int y = 50 + (imageIndex / 4 * (imageHeight + 30));

                RenderSystem.setShaderTexture(0, TEXTURE.getGlId());
                RenderSystem.enableBlend();
                drawTexture(matrixStack, x, y, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
                RenderSystem.disableBlend();

                drawExtensionBadge(matrixStack, FilenameUtils.getExtension(name), x - 10, y + 14);
                this.addDrawableChild(new ButtonWidget(x, y + 5 + imageHeight, imageWidth, 20, text, button -> openScreenshotScreen(screenshots.indexOf(currentPage.get(imageIndex)))));
            });

            drawCenteredText(matrixStack, MinecraftClient.getInstance().textRenderer, new TranslatableText("nicephore.gui.gallery.pages", index + 1, pagesOfScreenshots.size()), centerX, this.height / 2 + 105, Color.WHITE.getRGB());
        }
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void drawExtensionBadge(MatrixStack matrixStack, String extension, int x, int y) {
        if (config.getFilter() == ScreenshotFilter.BOTH){
            drawTextWithShadow(matrixStack, MinecraftClient.getInstance().textRenderer, new LiteralText(extension.toUpperCase()), x + 12, y - 12, Color.WHITE.getRGB());
            //renderTooltip(matrixStack, new LiteralText(extension.toUpperCase()), x, y);
        }
    }

    private void modIndex(int value){
        final int max = pagesOfScreenshots.size();
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

    private void openScreenshotScreen(int value){
        MinecraftClient.getInstance().setScreen(new ScreenshotScreen(value, index));
    }

    private int getIndex(){
        if (index >= pagesOfScreenshots.size() || index < 0){
            index = pagesOfScreenshots.size() - 1;
        }
        return index;
    }

    private void closeScreen(String textComponentId) {
        SCREENSHOT_TEXTURES.forEach(NativeImageBackedTexture::close);
        SCREENSHOT_TEXTURES.clear();

        this.onClose();
        PlayerHelper.sendHotbarMessage(new TranslatableText(textComponentId));
    }

    public static boolean canBeShow(){
        return SCREENSHOTS_DIR.exists() && SCREENSHOTS_DIR.list().length > 0;
    }
}