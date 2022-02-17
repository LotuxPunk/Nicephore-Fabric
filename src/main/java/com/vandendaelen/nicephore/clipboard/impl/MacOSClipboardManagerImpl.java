package com.vandendaelen.nicephore.clipboard.impl;

import com.vandendaelen.nicephore.clipboard.ClipboardManager;

import java.io.File;
import java.io.IOException;

public class MacOSClipboardManagerImpl implements ClipboardManager {

    private static MacOSClipboardManagerImpl INSTANCE;

    public static MacOSClipboardManagerImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MacOSClipboardManagerImpl();
        }
        return INSTANCE;
    }

    @Override
    public boolean clipboardImage(File screenshot) {

        final String[] cmd = {"osascript", "-e", String.format("tell app \"Finder\" to set the clipboard to ( POSIX file \"%s\" )", screenshot.getAbsolutePath())};

        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
