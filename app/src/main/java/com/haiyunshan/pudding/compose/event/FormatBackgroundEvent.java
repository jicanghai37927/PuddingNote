package com.haiyunshan.pudding.compose.event;

public class FormatBackgroundEvent {

    public static final String SRC_COLOR    = "color";
    public static final String SRC_TEXTURE  = "texture";

    public static final String SRC_USUAL    = "usual";
    public static final String SRC_PLATE    = "plate";
    public static final String SRC_PICKER   = "picker";

    public final String mSource;

    public final int mFGColor;
    public final int mBGColor;
    public final String mTextureId;

    public FormatBackgroundEvent(String source, int fgColor, int bgColor) {
        this.mSource = source;

        this.mTextureId = "";
        this.mFGColor = fgColor;
        this.mBGColor = bgColor;

    }

    public FormatBackgroundEvent(String source, String textureId, int fgColor, int bgColor) {
        this.mSource = source;

        this.mTextureId = textureId;
        this.mFGColor = fgColor;
        this.mBGColor = bgColor;

    }
}
