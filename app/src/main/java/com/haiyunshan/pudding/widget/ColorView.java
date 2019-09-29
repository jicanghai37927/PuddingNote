package com.haiyunshan.pudding.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.haiyunshan.pudding.R;

public class ColorView extends FrameLayout implements Checkable {

    ImageView mCheckedView;
    int mColor;

    public ColorView(@NonNull Context context) {
        this(context, null);
    }

    public ColorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ColorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.init(context);
    }

    void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.merge_color_item, this, true);

        this.mCheckedView = findViewById(R.id.iv_checked);
        mCheckedView.setVisibility(View.INVISIBLE);

        this.setClickable(true);
    }

    public void setColor(int color) {
        this.mColor = color;

        this.setBackgroundColor(color);
    }

    public int getColor() {
        return mColor;
    }

    @Override
    public void setChecked(boolean checked) {
        boolean v = isChecked();
        if (!(v ^ checked)) {
            return;
        }

        mCheckedView.setVisibility(checked? View.VISIBLE: View.INVISIBLE);
    }

    @Override
    public boolean isChecked() {
        return mCheckedView.getVisibility() == View.VISIBLE;
    }

    @Override
    public void toggle() {
        this.setChecked(!isChecked());
    }
}
