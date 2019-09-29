package com.haiyunshan.pudding.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.haiyunshan.pudding.R;

public class ColorLayout extends FrameLayout {

    LinearLayout mContainer;

    public ColorLayout(Context context) {
        this(context, null);
    }

    public ColorLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ColorLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.init(context);
    }

    void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.merge_color_group, this, true);

        this.mContainer = findViewById(R.id.container);
    }

    public LinearLayout getContainer() {
        return mContainer;
    }

    public void addChild(View child) {
        mContainer.addView(child);
    }

}
