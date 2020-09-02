package com.vandendaelen.nicephore.mixin;

import com.vandendaelen.nicephore.thread.JPEGThread;
import com.vandendaelen.nicephore.util.CopyImageToClipBoard;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

@Mixin(net.minecraft.client.util.ScreenshotUtils.class)
public class ScreenshotUtils {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    @Inject(method = "Lnet/minecraft/client/util/ScreenshotUtils;saveScreenshotInner(Ljava/io/File;Ljava/lang/String;IILnet/minecraft/client/gl/Framebuffer;Ljava/util/function/Consumer;)V", at = @At("TAIL"))
    private static void saveScreenshotInner(File gameDirectory, @Nullable String fileName, int framebufferWidth, int framebufferHeight, Framebuffer framebuffer, Consumer<Text> messageReceiver, CallbackInfo info) {
        final NativeImage image = takeScreenshot(framebufferWidth, framebufferHeight, framebuffer);
        final File screenshotsDir = new File(gameDirectory, "screenshots");

        File screenshotFile = null;
        if (fileName == null) {
            screenshotFile = new File(screenshotsDir, DATE_FORMAT.format(new Date()) + ".png");
        } else {
            screenshotFile = new File(screenshotsDir, fileName);
        }
        CopyImageToClipBoard.setLastScreenshot(screenshotFile);

        final JPEGThread thread = new JPEGThread(image, screenshotFile);
        thread.start();
    }

    @Shadow
    public static NativeImage takeScreenshot(int width, int height, Framebuffer framebuffer) {
        throw new IllegalStateException("Mixin failed to shadow takeScreenshot()");
    }
    @Shadow
    private static File getScreenshotFilename(File directory){
        throw new IllegalStateException("Mixin failed to shadow getScreenshotFilename()");
    }
}

