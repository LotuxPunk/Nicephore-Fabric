package com.vandendaelen.nicephore.config;

import com.vandendaelen.nicephore.Nicephore;
import com.vandendaelen.nicephore.util.ScreenshotFilter;
import com.vandendaelen.nicephore.util.Util;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = Nicephore.MOD_ID)
public class NicephoreConfig implements ConfigData {

    @Comment("Enable to allow Nicephore to make lossy JPEGs of your screenshots for easier online sharing. Disable to only allow PNGs."
            + "\r\nNote that PNGs will still be made regardless of this option.")
    boolean makeJPEGs = Util.getOS().equals(Util.OS.WINDOWS);

    @Comment("Automatically put newly made screenshots into your clipboard")
    boolean screenshotToClipboard = true;

    @Comment("JPEG compression level, the higher the number, the better the quality."
            + "\r\nNote that 1.0 is *not* lossless as JPEG is a lossy-only format, use the PNG files instead if you want lossless.")
    float jpegCompression = 0.9F;

    @Comment("Enable to allow Nicephore to losslessly optimise the PNG and JPEG screenshots for smaller sized progressive files that are of identical quality to the files before optimisation." +
            "\r\nNote: Enabling this will cause screenshots to take slightly longer to save as an optimisation step will have to be run first." +
            "\r\nTip: In the rare case that a screenshot PNG is corrupted, run \"oxipng --fix (filename).png\" to attempt to fix it.")
    boolean optimisedOutput = Util.getOS().equals(Util.OS.WINDOWS);

    @Comment("Only show the PNG, JPEG or JPEG/PNG on the screenshot GUI")
    ScreenshotFilter filter = ScreenshotFilter.BOTH;

    @Comment("If enabled, a message will appear above your hotbar telling you that has optimisation started and another when finished. Useful for very slow computers.")
    boolean showOptimisationStatus = true;

    @Comment("If optimiseScreenshots is enabled, use the following oxipng optimisation level, with higher numbers taking longer to process but give lower file sizes." +
            "\r\nTip: I would avoid anything above 3 unless you have a lot of CPU cores and threads (e.g. 16c/32t+) as it starts taking significantly longer to process for vastly diminishing returns.")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 5)
    int pngOptimisationLevel = 3;

    public boolean makeJPEGs() {
        return makeJPEGs;
    }

    public float getJpegCompression() {
        return jpegCompression;
    }

    public boolean isOptimisedOutput() {
        return optimisedOutput;
    }

    public boolean isShowOptimisationStatus() {
        return showOptimisationStatus;
    }

    public int getPngOptimisationLevel() {
        return pngOptimisationLevel;
    }

    public ScreenshotFilter getFilter() {
        return filter;
    }

    public void setFilter(ScreenshotFilter value) {
        filter = value;
    }

    public boolean isScreenshotToClipboard() {
        return screenshotToClipboard;
    }
}
