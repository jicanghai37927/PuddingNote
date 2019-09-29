package com.haiyunshan.pudding.divider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class SectionDividerItemDecoration extends RecyclerView.ItemDecoration {

    private static final String TAG = "DividerItem";
    private static final int[] ATTRS = new int[]{ android.R.attr.listDivider };

    private Drawable mDivider;
    private final Rect mBounds = new Rect();

    boolean mTop = true;

    public SectionDividerItemDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        if (mDivider == null) {
            Log.w(TAG, "@android:attr/listDivider was not set in the theme used for this "
                    + "DividerItemDecoration. Please set that attribute all call setDrawable()");
        }
        a.recycle();
    }

    /**
     * Sets the {@link Drawable} for this divider.
     *
     * @param drawable Drawable that should be used as a divider.
     */
    public void setDrawable(@NonNull Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException("Drawable cannot be null.");
        }

        mDivider = drawable;
    }

    public void setTop(boolean value) {
        this.mTop = value;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() == null || mDivider == null) {
            return;
        }

        drawVertical(c, parent);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mDivider == null) {
            outRect.set(0, 0, 0, 0);
            return;
        }

//        outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();

        final int left;
        final int right;

        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            parent.getDecoratedBoundsWithMargins(child, mBounds);
            final int bottom = mBounds.bottom + Math.round(child.getTranslationY());
            final int top = bottom - mDivider.getIntrinsicHeight();

            RecyclerView.ViewHolder h = parent.findContainingViewHolder(child);

            int x = left;
            int pos = parent.getChildAdapterPosition(child);
            if (pos + 1 < parent.getAdapter().getItemCount()) {
                if (h instanceof UpdateSectionMargin) {
                    UpdateSectionMargin holder = (UpdateSectionMargin)h;
                    x += holder.getMargin();
                }
            }

            mDivider.setBounds(x, top, right, bottom);
            mDivider.draw(canvas);

            if (pos == 0 && mTop) {
                mDivider.setBounds(left, mBounds.top, right, mBounds.top + mDivider.getIntrinsicHeight());
                mDivider.draw(canvas);
            }
        }

        canvas.restore();
    }

    public interface UpdateSectionMargin {

        int getMargin();

    }
}
