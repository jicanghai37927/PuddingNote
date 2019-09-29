package com.haiyunshan.pudding.compose.format;

import android.graphics.Color;
import android.text.Layout;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.haiyunshan.pudding.color.ColorUtils;
import com.haiyunshan.pudding.scheme.dataset.SchemeEntry;

/**
 * 文本格式内容
 *
 */
public class TextFormat {

    @SerializedName("font")
    String mFont;

    @SerializedName("textSize")
    int mTextSize;

    @SerializedName("textColor")
    String mTextColor;

    @SerializedName("bgColor")
    String mBackgroundColor;

    @SerializedName("bgTexture")
    String mBackgroundTexture;      // BackgroundManager ID

    @SerializedName("paddingLeft")
    int mPaddingLeft;

    @SerializedName("paddingRight")
    int mPaddingRight;

    @SerializedName("paddingTop")
    int mPaddingTop;

    @SerializedName("paddingBottom")
    int mPaddingBottom;

    @SerializedName("align")
    String mAlignment;

    @SerializedName("lineMultiplier")
    int mLineMult;

    @SerializedName("letterMultiplier")
    int mLetterMult;

    @SerializedName("frame")
    String mFrame;      // FrameManager ID

    @SerializedName("scheme")
    String mScheme;     // SchemeManager ID

    TextFormat() {

    }

    public String getFont() {
        if (mFont == null) {
            return "";
        }

        return mFont;
    }

    public void setFont(String font) {
        this.mFont = font;
    }

    public int getTextSize() {
        if (mTextSize <= 0) {
            return 20; // 默认值
        }

        return mTextSize;
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
    }

    public int getTextColor() {
        if (TextUtils.isEmpty(mTextColor)) {
            return Color.BLACK;
        }

        return ColorUtils.parseColor(mTextColor);
    }

    public void setTextColor(int color) {
        String value = ColorUtils.fromColor(color);
        this.mTextColor = value;
    }

    public int getBackgroundColor() {
        if (TextUtils.isEmpty(mBackgroundColor)) {
            return Color.TRANSPARENT;
        }

        return ColorUtils.parseColor(mBackgroundColor);
    }

    public void setBackgroundColor(int color) {
        String value = ColorUtils.fromColor(color);
        this.mBackgroundColor = value;
    }

    public String getBackgroundTexture() {
        if (mBackgroundTexture == null) {
            return "";
        }

        return mBackgroundTexture;
    }

    public void setBackgroundTexture(String uri) {
        this.mBackgroundTexture = uri;
    }

    public int getPaddingHorizontal() {
        if (mPaddingLeft <= 0 || mPaddingRight <= 0) {
            return 8;
        }

        return mPaddingLeft;
    }

    public void setPaddingHorizontal(int padding) {
        this.mPaddingLeft = padding;
        this.mPaddingRight = padding;
    }

    public int getPaddingVertical() {
        if (mPaddingTop <= 0 || mPaddingBottom <= 0) {
            return 8;
        }

        return mPaddingTop;
    }

    public void setPaddingVertical(int padding) {
        this.mPaddingTop = padding;
        this.mPaddingBottom = padding;
    }

    public Layout.Alignment getAlignment() {
        if (TextUtils.isEmpty(mAlignment)) {
            return Layout.Alignment.ALIGN_NORMAL;
        }

        if (mAlignment.equalsIgnoreCase("center")) {

            return Layout.Alignment.ALIGN_CENTER;

        } else if (mAlignment.equalsIgnoreCase("opposite")) {

            return Layout.Alignment.ALIGN_OPPOSITE;

        }

        return Layout.Alignment.ALIGN_NORMAL;
    }

    public void setAlignment(Layout.Alignment align) {
        if (align == Layout.Alignment.ALIGN_NORMAL) {
            this.mAlignment = "normal";
        } else if (align == Layout.Alignment.ALIGN_OPPOSITE) {
            this.mAlignment = "opposite";
        } else {
            this.mAlignment = "center";
        }
    }

    public int getLineSpacingMultiplier() {
        if (mLineMult <= 0) {
            return 100;
        }

        return mLineMult;
    }

    public void setLineSpacingMultiplier(int value) {
        this.mLineMult = value;
    }

    public int getLetterSpacingMultiplier() {
        if (mLetterMult <= 0) {
            return 100;
        }

        return mLetterMult;
    }

    public void setLetterSpacingMultiplier(int value) {
        this.mLetterMult = value;
    }

    public String getFrame() {
        if (TextUtils.isEmpty(mFrame)) {
            return "";
        }

        return mFrame;
    }

    public void setFrame(String frameId) {
        this.mFrame = frameId;
    }

    public String getScheme() {
        if (TextUtils.isEmpty(mScheme)) {
            return "";
        }

        return mScheme;
    }

    public void setScheme(String scheme) {
        this.mScheme = scheme;
    }

    public void setScheme(SchemeEntry scheme) {
        this.mScheme = scheme.getId();

        // 字体无关
        {
            this.setTextColor(scheme.getTextColor());
            this.setBackgroundColor(scheme.getBackgroundColor());
            this.mBackgroundTexture = scheme.getBackgroundTexture();
            this.mFrame = scheme.getFrame();
            this.setAlignment(scheme.getAlignment());
        }

        // 字体相关
        {
            this.mFont = scheme.getFont();
            this.mTextSize = scheme.getTextSize();
            this.mPaddingLeft = scheme.getPaddingLeft();
            this.mPaddingRight = scheme.getPaddingRight();
            this.mPaddingTop = scheme.getPaddingTop();
            this.mPaddingBottom = scheme.getPaddingBottom();
            this.mLineMult = scheme.getLineSpacingMultiplier();
            this.mLetterMult = scheme.getLetterSpacingMultiplier();
        }

    }

    public void setFormat(TextFormat format) {

        this.mFont = format.mFont;

        this.mTextSize = format.mTextSize;

        this.mTextColor = format.mTextColor;

        this.mBackgroundColor = format.mBackgroundColor;

        this.mBackgroundTexture = format.mBackgroundTexture;      // BackgroundManager ID

        this.mPaddingLeft = format.mPaddingLeft;

        this.mPaddingRight = format.mPaddingRight;

        this.mPaddingTop = format.mPaddingTop;

        this.mPaddingBottom = format.mPaddingBottom;

        this.mAlignment = format.mAlignment;

        this.mLineMult = format.mLineMult;

        this.mLetterMult = format.mLetterMult;

        this.mFrame = format.mFrame;      // FrameManager ID

        this.mScheme = format.mScheme;     // SchemeManager ID
    }
}
