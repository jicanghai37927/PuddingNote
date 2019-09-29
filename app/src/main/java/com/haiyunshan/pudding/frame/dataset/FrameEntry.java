package com.haiyunshan.pudding.frame.dataset;

import com.google.gson.annotations.SerializedName;
import com.haiyunshan.pudding.dataset.BaseEntry;

/**
 *
 */
public class FrameEntry extends BaseEntry {

    @SerializedName("name")
    String mName;

    @SerializedName("uri")
    String mUri;

    @SerializedName("scale")
    float mScale;

    @SerializedName("margin")
    Insets mMargin;

    @SerializedName("padding")
    Insets mPadding;

    transient Insets mInsets;

    public FrameEntry(String id) {
        super(id);

        this.mUri = "";
        this.mScale = 1;
        this.mMargin = new Insets(16, 24, 16, 24);
        this.mPadding = new Insets(0, 0, 0, 0);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getUri() {
        if (mUri == null) {
            return "";
        }

        return mUri;
    }

    public float getScale() {
        if (mScale < 0) {
            return 0;
        }

        if (mScale > 1) {
            return 1;
        }

        return mScale;
    }

    public Insets getMargin() {
        if (mMargin == null) {
            mMargin = new Insets();
        }

        return mMargin;
    }

    public Insets getPadding() {
        if (mPadding == null) {
            mPadding = new Insets();
        }

        return mPadding;
    }

    public Insets getInsets() {
        if (mInsets == null) {
            Insets margin = getMargin();
            Insets padding = getPadding();

            int top = margin.mTop + padding.mTop;
            int left = margin.mLeft + padding.mLeft;
            int bottom = margin.mBottom + padding.mBottom;
            int right = margin.mRight + padding.mRight;

            this.mInsets = new Insets(top, left, bottom, right);
        }

        return mInsets;
    }

    public static class Insets {

        @SerializedName("top")
        public int mTop;

        @SerializedName("left")
        public int mLeft;

        @SerializedName("bottom")
        public int mBottom;

        @SerializedName("right")
        public int mRight;

        public Insets() {
            this(0, 0, 0, 0);
        }

        public Insets(int top, int left, int bottom, int right) {
            this.mTop = top;
            this.mLeft = left;
            this.mBottom = bottom;
            this.mRight = right;
        }
    }
}
