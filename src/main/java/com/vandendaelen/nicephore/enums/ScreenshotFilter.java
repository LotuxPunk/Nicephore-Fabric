package com.vandendaelen.nicephore.enums;

import java.io.FilenameFilter;

public enum ScreenshotFilter {
    JPEG((dir, name) -> name.endsWith(".jpg")),
    PNG((dir, name) -> name.endsWith(".png")),
    BOTH((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png"));

    private FilenameFilter predicate;

    ScreenshotFilter(FilenameFilter predicate) {
        this.predicate = predicate;
    }

    public FilenameFilter getPredicate() {
        return predicate;
    }

    public ScreenshotFilter next() {
        return values()[(this.ordinal() + 1) % values().length];
    }
}
