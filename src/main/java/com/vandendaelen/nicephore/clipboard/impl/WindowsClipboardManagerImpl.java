package com.vandendaelen.nicephore.clipboard.impl;

import com.profesorfalken.jpowershell.PowerShell;
import com.vandendaelen.nicephore.clipboard.ClipboardManager;
import net.minecraft.util.Util;

import java.io.File;

public class WindowsClipboardManagerImpl implements ClipboardManager {
    private static WindowsClipboardManagerImpl INSTANCE;
    private final PowerShell session;

    public WindowsClipboardManagerImpl() {
        if (Util.getOperatingSystem() == Util.OperatingSystem.WINDOWS) {
            this.session = PowerShell.openSession();
        } else {
            this.session = null;
        }
    }

    public static WindowsClipboardManagerImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WindowsClipboardManagerImpl();
        }
        return INSTANCE;
    }

    @Override
    public boolean clipboardImage(File screenshot) {
        final String command = "[Reflection.Assembly]::LoadWithPartialName('System.Drawing');\n" +
                "[Reflection.Assembly]::LoadWithPartialName('System.Windows.Forms');\n" +
                "\n" +
                String.format("$filename = \"%s\";\n", screenshot.getAbsolutePath()) +
                "$file = get-item($filename);\n" +
                "$img = [System.Drawing.Image]::Fromfile($file);\n" +
                "[System.Windows.Forms.Clipboard]::SetImage($img);";

        return !session.executeCommand(command).isError();
    }
}
