package com.vandendaelen.nicephore.thread;

import com.vandendaelen.nicephore.config.NicephoreConfig;
import com.vandendaelen.nicephore.helper.PlayerHelper;
import com.vandendaelen.nicephore.util.CopyImageToClipBoard;
import com.vandendaelen.nicephore.util.Reference;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

public final class JPEGThread extends Thread {
    private final NativeImage image;
    private final File screenshot;

    public JPEGThread(NativeImage image, File screenshot) {
        this.image = image;
        this.screenshot = screenshot;
    }

    @Override
    public void run() {
        NicephoreConfig config = AutoConfig.getConfigHolder(NicephoreConfig.class).getConfig();
        try {
            final ByteArrayInputStream bais = new ByteArrayInputStream(image.getBytes());
            final BufferedImage png = ImageIO.read(bais);
            final File jpegFile = new File(screenshot.getParentFile(), screenshot.getName().replace("png", "jpg"));
            final BufferedImage result = new BufferedImage(
                    png.getWidth(),
                    png.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            result.createGraphics().drawImage(png, 0, 0, Color.WHITE, null);

            // only run JPEG creation-related code if "makeJPEGs" is true in the config
            if (config.makeJPEGs()) {
                final ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
                final ImageWriteParam params = writer.getDefaultWriteParam();
                params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                params.setProgressiveMode(ImageWriteParam.MODE_DEFAULT);
                params.setCompressionQuality(config.getJpegCompression());
                writer.setOutput(new FileImageOutputStream(jpegFile));
                writer.write(null, new IIOImage(result, null, null), params);
            }

            // only run optimisation-related code if "optimiseScreenshots" is true in the config
            if (config.isOptimisedOutput()) {
                if (config.isShowOptimisationStatus()) {
                    PlayerHelper.sendHotbarMessage(Text.translatable("nicephore.screenshot.optimize"));
                }

                // only run JPEG optimisation with ECT if we "makeJPEGs" is true in the config
                if (config.makeJPEGs()) {

                    // attempt to optimise the JPEG screenshot using ECT
                    try {
                        final File ect = new File(String.format("mods%snicephore%s", File.separator, File.separator) + Reference.File.ECT);
                        // ECT is lightning fast for small JPEG files so we might as well use optimisation level 9
                        final Process p = Runtime.getRuntime().exec(MessageFormat.format(Reference.Command.ECT, ect, jpegFile));
                        p.waitFor();
                    } catch (IOException | InterruptedException e) {
//                        Nicephore.LOGGER.warn("Unable to optimise screenshot JPEG with ECT. Is it missing from the mods folder?");
//                        Nicephore.LOGGER.warn(e.getMessage());
                    }
                }

                // attempt to optimise the PNG screenshot using Oxipng
                try {
                    final File oxipng = new File(String.format("mods%snicephore%s", File.separator, File.separator) + Reference.File.OXIPNG);
                    final File pngFile = new File(screenshot.getParentFile(), screenshot.getName());
                    final Process p = Runtime.getRuntime().exec(MessageFormat.format(Reference.Command.OXIPNG, oxipng, config.getPngOptimisationLevel(), pngFile));
                    p.waitFor();
                } catch (IOException | InterruptedException e) {
//                    Nicephore.LOGGER.warn("Unable to optimise screenshot PNG with Oxipng. Is it missing from the mods folder?");
//                    Nicephore.LOGGER.warn(e.getMessage());
                }

                if (config.isShowOptimisationStatus()) {
                    PlayerHelper.sendHotbarMessage(Text.translatable("nicephore.screenshot.optimizeFinished"));
                }
            }

            CopyImageToClipBoard.getInstance().setLastScreenshot(screenshot);
            if (config.isScreenshotCustomMessage()) {
                if (config.isScreenshotToClipboard()) {
                    if (CopyImageToClipBoard.getInstance().copyLastScreenshot()) {
                        PlayerHelper.sendMessage(Text.translatable("nicephore.clipboard.success").formatted(Formatting.GREEN));
                    } else {
                        PlayerHelper.sendMessage(Text.translatable("nicephore.clipboard.error").formatted(Formatting.RED));
                    }
                }

                final MutableText pngComponent = (Text.translatable("nicephore.screenshot.png")).formatted(Formatting.UNDERLINE).styled((style)
                        -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, screenshot.getAbsolutePath())));

                final MutableText jpgComponent = (Text.translatable("nicephore.screenshot.jpg")).formatted(Formatting.UNDERLINE).styled((style)
                        -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, jpegFile.getAbsolutePath())));

                final MutableText folderComponent = (Text.translatable("nicephore.screenshot.folder")).formatted(Formatting.UNDERLINE).styled((style)
                        -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, screenshot.getParent())));


                if (config.makeJPEGs()) {
                    PlayerHelper.sendMessage(Text.translatable("nicephore.screenshot.options", pngComponent, jpgComponent, folderComponent));
                } else {
                    PlayerHelper.sendMessage(Text.translatable("nicephore.screenshot.reducedOptions", pngComponent, folderComponent));
                }
            }
        } catch (IOException e) {
            PlayerHelper.sendMessage(Text.translatable("nicephore.screenshot.error"));
        }
    }
}