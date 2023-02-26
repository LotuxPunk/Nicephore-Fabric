package com.vandendaelen.nicephore.util;

import com.vandendaelen.nicephore.enums.OperatingSystems;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;

public class CopyImageToClipBoard implements ClipboardOwner {
    private static File lastScreenshot = null;
    private static CopyImageToClipBoard instance;

    public static CopyImageToClipBoard getInstance() {
        if (instance == null) {
            instance = new CopyImageToClipBoard();
        }
        return instance;
    }

    public void setLastScreenshot(File screenshot) {
        lastScreenshot = screenshot;
    }

    public boolean isLastScreenshot(File screenshot) { return screenshot.equals(lastScreenshot); }

    public boolean copyImage(File screenshot) {
        if (OperatingSystems.getOS().getManager() != null) {
            OperatingSystems.getOS().getManager().clipboardImage(screenshot);
            return true;
        }
        return false;
    }

    public boolean copyLastScreenshot() {
        if (lastScreenshot != null) {
            return copyImage(lastScreenshot);
        }
        return false;
    }

    public void lostOwnership(Clipboard clip, Transferable trans) {
        System.out.println("Lost Clipboard Ownership");
    }

    private record TransferableImage(Image i) implements Transferable {

        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (flavor.equals(DataFlavor.imageFlavor) && i != null) {
                return i;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] flavors = new DataFlavor[1];
            flavors[0] = DataFlavor.imageFlavor;
            return flavors;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            DataFlavor[] flavors = getTransferDataFlavors();
            for (DataFlavor dataFlavor : flavors) {
                if (flavor.equals(dataFlavor)) {
                    return true;
                }
            }
            return false;
        }
    }
}
