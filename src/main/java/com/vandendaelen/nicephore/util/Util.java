package com.vandendaelen.nicephore.util;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Util {

    public static NativeImageBackedTexture fileToTexture(File file) {
        NativeImage nativeImage = null;
        try {
            nativeImage = NativeImage.read(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new NativeImageBackedTexture(nativeImage);
    }

    public static <T> Stream<List<T>> batches(List<T> source, int length) {
        if (length <= 0)
            throw new IllegalArgumentException("length = " + length);
        int size = source.size();
        if (size <= 0)
            return Stream.empty();
        int fullChunks = (size - 1) / length;
        return IntStream.range(0, fullChunks + 1).mapToObj(
                n -> source.subList(n * length, n == fullChunks ? size : (n + 1) * length));
    }

    private static Dimension getImageDimensions(Object input) throws IOException {

        try (ImageInputStream stream = ImageIO.createImageInputStream(input)) { // accepts File, InputStream, RandomAccessFile
            if(stream != null) {
                IIORegistry iioRegistry = IIORegistry.getDefaultInstance();
                Iterator<ImageReaderSpi> iter = iioRegistry.getServiceProviders(ImageReaderSpi.class, true);
                while (iter.hasNext()) {
                    ImageReaderSpi readerSpi = iter.next();
                    if (readerSpi.canDecodeInput(stream)) {
                        ImageReader reader = readerSpi.createReaderInstance();
                        try {
                            reader.setInput(stream);
                            int width = reader.getWidth(reader.getMinIndex());
                            int height = reader.getHeight(reader.getMinIndex());
                            return new Dimension(width, height);
                        } finally {
                            reader.dispose();
                        }
                    }
                }
                throw new IllegalArgumentException("Can't find decoder for this image");
            } else {
                throw new IllegalArgumentException("Can't open stream for this image");
            }
        }
    }
}
