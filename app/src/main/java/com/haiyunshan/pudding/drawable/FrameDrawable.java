package com.haiyunshan.pudding.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class FrameDrawable extends Drawable {

    Drawable mDrawable;
    Rect mRect;

    Rect mTmpRect;

    public FrameDrawable() {
        this.mDrawable = null;
        this.mRect = new Rect();

        this.mTmpRect = new Rect();
    }

    public void setDrawable(Drawable d) {
        this.setDrawable(d, 0, 0, 0, 0);
    }

    public void setDrawable(Drawable d, int left, int top, int right, int bottom) {
        this.mDrawable = d;
        this.mRect.set(left, top, right, bottom);

        if (mDrawable != null) {
            this.applyBounds(this.getBounds());
        }
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        if (mDrawable != null) {
            this.applyBounds(bounds);
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mDrawable != null) {
            mDrawable.draw(canvas);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        if (mDrawable != null) {
            mDrawable.setAlpha(alpha);
        }
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        if (mDrawable != null) {
            mDrawable.setColorFilter(colorFilter);
        }
    }

    @Override
    public int getOpacity() {
        if (mDrawable != null) {
            return mDrawable.getOpacity();
        }

        return 0;
    }

    void applyBounds(Rect bounds) {
        Rect rect = mTmpRect;
        rect.set(bounds);

        rect.left += mRect.left;
        rect.top += mRect.top;
        rect.right -= mRect.right;
        rect.bottom -= mRect.bottom;

        mDrawable.setBounds(rect);
    }
}
