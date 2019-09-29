package com.haiyunshan.pudding.compose.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

/**
 *
 */
public class ParagraphEditLayout extends FrameLayout {

    EditText mEdit;
    Rect mTempRect = new Rect();

    public ParagraphEditLayout(@NonNull Context context) {
        this(context, null);
    }

    public ParagraphEditLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParagraphEditLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ParagraphEditLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mEdit == null) {
            this.mEdit = this.findEditText(this);
        }

        if (this.mEdit == null) {
            return super.dispatchTouchEvent(ev);
        }

        View rectView = mEdit;
        mTempRect.set(0, 0, rectView.getWidth(), rectView.getHeight());

        offsetDescendantRectToMyCoords(mEdit, mTempRect);
//        offsetRectIntoDescendantCoords(mEdit, mTempRect);

        int x = (int)ev.getX();
        int y = (int)ev.getY();
        if (mTempRect.contains(x, y)) {
            return super.dispatchTouchEvent(ev);
        } else {

            MotionEvent event = MotionEvent.obtain(ev);

            if (x < mTempRect.left) {
                event.offsetLocation(mTempRect.left - x, 0);
            }
            if (x > mTempRect.right) {
                event.offsetLocation(mTempRect.right - x - 1, 0);
            }
            if (y < mTempRect.top) {
                event.offsetLocation(0, mTempRect.top - y);
            }
            if (y > mTempRect.bottom) {
                event.offsetLocation(0, mTempRect.bottom - y - 1);
            }

            boolean result = super.dispatchTouchEvent(event);
            event.recycle();

            return result;
        }
    }

    EditText findEditText(View view) {

        if (view instanceof EditText) {
            return (EditText)view;
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup)view;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                View v = group.getChildAt(i);
                return findEditText(v);
            }
        }

        return null;
    }

}
