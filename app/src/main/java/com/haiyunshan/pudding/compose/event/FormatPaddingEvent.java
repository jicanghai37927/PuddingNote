package com.haiyunshan.pudding.compose.event;

public class FormatPaddingEvent {

    public final int mPaddingLeft;
    public final int mPaddingRight;
    public final int mPaddingTop;
    public final int mPaddingBottom;

    public FormatPaddingEvent(int left, int right, int top, int bottom) {
        this.mPaddingLeft = left;
        this.mPaddingRight = right;

        this.mPaddingTop = top;
        this.mPaddingBottom = bottom;
    }

    public FormatPaddingEvent(int horizontal, int vertical) {
        this(horizontal, horizontal, vertical, vertical);
    }
}
