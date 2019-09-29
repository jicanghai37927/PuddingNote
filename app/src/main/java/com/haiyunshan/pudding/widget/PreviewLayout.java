package com.haiyunshan.pudding.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.haiyunshan.pudding.drawable.FrameDrawable;
import com.haiyunshan.pudding.utils.WindowUtils;

public class PreviewLayout extends LinearLayout {

    FrameDrawable mFrameDrawable;

    public PreviewLayout(Context context) {
        this(context, null);
    }

    public PreviewLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreviewLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PreviewLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.init(context);
    }

    void init(Context context) {
        this.mFrameDrawable = new FrameDrawable();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mFrameDrawable != null) {
            mFrameDrawable.setBounds(0, 0, w, h);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        this.drawFrame(canvas);

        super.onDraw(canvas);
    }

    public FrameDrawable getFrameDrawable() {
        return mFrameDrawable;
    }

    void drawFrame(Canvas canvas) {
        if (mFrameDrawable == null || mFrameDrawable.getDrawable() == null) {
            return;
        }

        mFrameDrawable.draw(canvas);
    }
}
