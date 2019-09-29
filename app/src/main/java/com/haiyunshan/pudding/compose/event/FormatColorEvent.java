package com.haiyunshan.pudding.compose.event;

public class FormatColorEvent {

    public static final String SRC_USUAL = "usual";
    public static final String SRC_PLATE = "plate";
    public static final String SRC_PICKER = "picker";

    public final String mSource;
    public final int mColor;

    public FormatColorEvent(String source, int color) {
        this.mSource = source;
        this.mColor = color;
    }
}
