package com.vandendaelen.nicephore.enums;

import com.vandendaelen.nicephore.clipboard.ClipboardManager;
import com.vandendaelen.nicephore.clipboard.impl.MacOSClipboardManagerImpl;
import com.vandendaelen.nicephore.clipboard.impl.WindowsClipboardManagerImpl;
import net.minecraft.util.Util;

public enum OperatingSystems {
    WINDOWS(WindowsClipboardManagerImpl.getInstance()),
    LINUX(null),
    MAC(MacOSClipboardManagerImpl.getInstance()),
    SOLARIS(null);

    private static OperatingSystems OS;
    private final ClipboardManager manager;

    OperatingSystems(ClipboardManager instance) {
        this.manager = instance;
    }

    public static OperatingSystems getOS() {
        return switch (Util.getOperatingSystem()) {
            case WINDOWS -> WINDOWS;
            case LINUX -> LINUX;
            case OSX -> MAC;
            case SOLARIS -> SOLARIS;
            default -> throw new IllegalStateException("Unexpected value: " + Util.getOperatingSystem());
        };
    }

    public ClipboardManager getManager() {
        return manager;
    }
}
