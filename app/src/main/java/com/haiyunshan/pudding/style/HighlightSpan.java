package com.haiyunshan.pudding.style;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.UpdateAppearance;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.App;

public class HighlightSpan implements LineSpan, UpdateAppearance {

    Drawable mDrawable;

    Rect mRect;

    public HighlightSpan() {

        int resId = R.drawable.highlight_bg_brown_1;
        this.mDrawable = App.getInstance().getDrawable(resId);

        this.mRect = new Rect();
    }

    public HighlightSpan(HighlightSpan span) {
        this.mDrawable = span.mDrawable;

        this.mRect = new Rect();
    }

    @Override
    public void draw(Canvas c, Layout layout, TextPaint p,
                     int top, int baseline, int bottom,
                     Spanned text, int start, int end, int lnum) {

        if (layout == null) {
            return;
        }

        if (isEmpty(text, start, end)) {
            return;
        }

        int spanStart = text.getSpanStart(this);
        int spanEnd = text.getSpanEnd(this);

        if (start < spanStart) {
            start = spanStart;
        }

        if (end > spanEnd) {
            end = spanEnd;
        } else {
            if ((end > 0 && end < text.length()) && text.charAt(end - 1) == '\n') {
                end -= 1;
            }
        }

        Rect r = this.mRect;

        int trick = (int)((baseline - top) * 0.16f);

        r.top = top;
        r.bottom = baseline + trick;
        r.left = (int) layout.getPrimaryHorizontal(start);
        r.right = (int) layout.getSecondaryHorizontal(end);
        if (r.right == 0) {
            r.right = (int)layout.getLineRight(lnum);
        }

        mDrawable.setBounds(r);
        mDrawable.draw(c);
    }

    boolean isEmpty(Spanned text, int start, int end) {
        boolean result = true;

        for (int i = start; i < end; i++) {
            char c = text.charAt(i);
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                continue;
            }

            result = false;
            break;
        }

        return result;
    }


}
