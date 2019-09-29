package com.haiyunshan.pudding.scheme.dataset;

import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.text.Layout;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.haiyunshan.pudding.color.ColorUtils;
import com.haiyunshan.pudding.dataset.BaseEntry;

/**
 *
 */
public class SchemeEntry extends BaseEntry {

    @SerializedName("name")
    String mName;

    @SerializedName("text")
    String mText;

    @SerializedName("textColor")
    String mTextColor;

    @SerializedName("bgColor")
    String mBackgroundColor;

    @SerializedName("bgTexture")
    String mBackgroundTexture;      // BackgroundManager ID

    @SerializedName("frame")
    String mFrame;  // FrameManager ID

    @SerializedName("align")
    String mAlignment;

    @SerializedName("font")
    String mFont;

    @SerializedName("textSize")
    int mTextSize;

    @SerializedName("paddingLeft")
    int mPaddingLeft;

    @SerializedName("paddingRight")
    int mPaddingRight;

    @SerializedName("paddingTop")
    int mPaddingTop;

    @SerializedName("paddingBottom")
    int mPaddingBottom;

    @SerializedName("lineMultiplier")
    int mLineMult;

    @SerializedName("letterMultiplier")
    int mLetterMult;

    @SerializedName("prefer")
    PreferFont mPrefer;

    transient CharSequence mPreviewText;

    public SchemeEntry(String id) {
        super(id);

        this.mName = "";
        this.mText = "";

        {
            this.mTextColor = "#000000";
            this.mBackgroundColor = "#00000000";
            this.mBackgroundTexture = "";
            this.mFrame = "";
            this.mAlignment = "normal";
        }

        {
            this.mFont = "";
            this.mTextSize = 20;
            this.mPaddingLeft = 36;
            this.mPaddingRight = 36;
            this.mPaddingTop = 18;
            this.mPaddingBottom = 18;
            this.mLineMult = 120;
            this.mLetterMult = 100;
        }

        {
            this.mPrefer = null;
        }

        {
            this.mPreviewText = null;
        }
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public CharSequence getText() {
        if (mPreviewText != null) {
            return mPreviewText;
        }

        String text = mText;
        text = (TextUtils.isEmpty(text)? "": text);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mPreviewText = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
        } else {
            mPreviewText = Html.fromHtml(text);
        }

        return mPreviewText;
    }

    public void setText(String text) {
        this.mText = text;
    }

    public int getTextColor() {
        if (TextUtils.isEmpty(mTextColor)) {
            return Color.BLACK;
        }

        return ColorUtils.parseColor(mTextColor);
    }

    public int getBackgroundColor() {
        if (TextUtils.isEmpty(mBackgroundColor)) {
            return Color.TRANSPARENT;
        }

        return ColorUtils.parseColor(mBackgroundColor);
    }

    public String getBackgroundTexture() {
        if (mBackgroundTexture == null) {
            return "";
        }

        return mBackgroundTexture;
    }

    public String getFrame() {
        if (TextUtils.isEmpty(mFrame)) {
            return "";
        }

        return mFrame;
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

    public String getFont() {
        if (mFont == null) {
            return "";
        }

        return mFont;
    }

    public int getTextSize() {
        if (mTextSize <= 0) {
            return 20; // 默认值
        }

        return mTextSize;
    }

    public int getPaddingLeft() {
        return mPaddingLeft;
    }

    public int getPaddingRight() {
        return mPaddingRight;
    }

    public int getPaddingTop() {
        return mPaddingTop;
    }

    public int getPaddingBottom() {
        return mPaddingBottom;
    }

    public int getLineSpacingMultiplier() {
        if (mLineMult <= 0) {
            return 100;
        }

        return mLineMult;
    }

    public int getLetterSpacingMultiplier() {
        if (mLetterMult <= 0) {
            return 100;
        }

        return mLetterMult;
    }

    public PreferFont getPrefer() {
        return mPrefer;
    }

    /**
     *
     */
    public static class PreferFont {

        @SerializedName("fontName")
        public String mFontName;

        @SerializedName("textSize")
        public int mTextSize;

        @SerializedName("paddingLeft")
        public int mPaddingLeft;

        @SerializedName("paddingRight")
        public int mPaddingRight;

        @SerializedName("paddingTop")
        public int mPaddingTop;

        @SerializedName("paddingBottom")
        public int mPaddingBottom;

        @SerializedName("lineMultiplier")
        public int mLineMult;

        @SerializedName("letterMultiplier")
        public int mLetterMult;

    }
}
