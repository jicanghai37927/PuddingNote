package com.haiyunshan.pudding.style;

import android.graphics.Canvas;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextPaint;

public interface LineSpan {

    /**
     *
     * @param c
     * @param layout
     * @param p
     * @param top
     * @param baseline
     * @param bottom
     * @param text
     * @param start
     * @param end
     * @param lnum
     */
    void draw(Canvas c, Layout layout, TextPaint p,
                     int top, int baseline, int bottom,
                     Spanned text, int start, int end, int lnum);
}
