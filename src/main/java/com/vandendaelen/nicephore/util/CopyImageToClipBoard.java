package com.vandendaelen.nicephore.util;

import net.minecraft.client.MinecraftClient;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class CopyImageToClipBoard implements ClipboardOwner {
    private static File lastScreenshot = null;
    private static CopyImageToClipBoard instance;

    public static CopyImageToClipBoard getInstance() {
        if (instance == null){
            instance = new CopyImageToClipBoard();
        }
        return instance;
    }

    public void setLastScreenshot(File screenshot){
        lastScreenshot = screenshot;
    }

    public boolean copyImage(BufferedImage bi) {
        if (!MinecraftClient.IS_SYSTEM_MAC && Objects.equals(System.getProperty("java.awt.headless"), "false")){
            final TransferableImage trans = new TransferableImage(bi);
            final Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
            c.setContents( trans, this );
            return true;
        }
        return false;
    }

    public boolean copyLastScreenshot() throws IOException {
        if ( lastScreenshot != null ) {
            return copyImage(ImageIO.read(lastScreenshot));
        }
        return false;
    }

    public void lostOwnership( Clipboard clip, Transferable trans ) {
        System.out.println( "Lost Clipboard Ownership" );
    }

    private class TransferableImage implements Transferable {

        final Image i;
        public TransferableImage( Image i ) {
            this.i = i;
        }

        public Object getTransferData( DataFlavor flavor ) throws UnsupportedFlavorException, IOException {
            if ( flavor.equals( DataFlavor.imageFlavor ) && i != null ) {
                return i;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] flavors = new DataFlavor[ 1 ];
            flavors[ 0 ] = DataFlavor.imageFlavor;
            return flavors;
        }

        public boolean isDataFlavorSupported( DataFlavor flavor ) {
            DataFlavor[] flavors = getTransferDataFlavors();
            for ( DataFlavor dataFlavor : flavors ) {
                if ( flavor.equals(dataFlavor) ) {
                    return true;
                }
            }
            return false;
        }
    }
}
