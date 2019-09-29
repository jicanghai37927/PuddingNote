package com.haiyunshan.pudding.compose.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.haiyunshan.pudding.drawable.FrameDrawable;
import com.haiyunshan.pudding.utils.WindowUtils;

/**
 *
 */
public class ComposeRecyclerView extends RecyclerView {

    View mTargetChild;
    Rect mTempRect = new Rect();

    View.OnTouchListener mOnTouchListener;
    OnNestedScrollListener mOnNestedScrollListener;

    int mDisplayHeight = 0;
    boolean mBackgroundSizeChanged = true;
    Drawable mBackground = null;

    FrameDrawable mFrameDrawable;

    public ComposeRecyclerView(Context context) {
        this(context, null);
    }

    public ComposeRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ComposeRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.init(context);
    }

    void init(Context context) {
        this.mDisplayHeight = WindowUtils.getRealHeight((Activity)context);

        this.mBackgroundSizeChanged = true;
        this.mBackground = null;

        this.mFrameDrawable = new FrameDrawable();
    }

    public FrameDrawable getFrameDrawable() {
        return mFrameDrawable;
    }

    public void setScrollBackground(Drawable d) {
        this.mBackground = d;
        this.mBackgroundSizeChanged = true;

        this.invalidate();
    }

    public void setOnNestedScrollListener(OnNestedScrollListener listener) {
        this.mOnNestedScrollListener = listener;
    }

    public void setOnDispatchTouchListener(OnTouchListener listener) {
        this.mOnTouchListener = listener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mOnTouchListener != null) {
            mOnTouchListener.onTouch(this, ev);
        }

        boolean result = super.dispatchTouchEvent(ev);

        int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                this.mTargetChild = findTargetChild(ev);

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (mTargetChild != null) {

                    int state = this.getScrollState();
                    if (state != SCROLL_STATE_IDLE) {

                        MotionEvent event = MotionEvent.obtain(ev);
                        event.offsetLocation(mTempRect.left, -mTempRect.top); // 转换到Child坐标
                        event.setAction(MotionEvent.ACTION_CANCEL);

                        mTargetChild.dispatchTouchEvent(event);
                        mTargetChild = null;

                        event.recycle();

                    }
                }

                break;
            }

        }

        if (mTargetChild != null) {
            MotionEvent event = MotionEvent.obtain(ev);
            event.offsetLocation(mTempRect.left, -mTempRect.top); // 转换到Child坐标

            mTargetChild.dispatchTouchEvent(event);

            event.recycle();
        }

        return result;
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        if (mOnNestedScrollListener != null) {
            mOnNestedScrollListener.onNestedFling(this, velocityX, velocityY, consumed);
        }

        return super.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        if (mOnNestedScrollListener != null) {
            mOnNestedScrollListener.onNestedPreFling(this, velocityX, velocityY);
        }

        return super.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        this.mBackgroundSizeChanged = true;

        super.onScrolled(dx, dy);
    }

    @Override
    public void onDraw(Canvas canvas) {
        this.drawBackground(canvas);

        this.drawFrame(canvas);

        super.onDraw(canvas);
    }

    void drawFrame(Canvas canvas) {
        if (mFrameDrawable == null || mFrameDrawable.getDrawable() == null) {
            return;
        }

        {
            int width = this.getWidth();

            // 滚动距离
            int range = this.computeVerticalScrollRange();

            // 最后一个控件位置
            if (this.getChildCount() != 0) {
                int index = getChildCount() - 1;
                View view = this.getChildAt(index);
                int bottom = view.getBottom();
                int offset = this.computeVerticalScrollOffset();

                bottom += offset;
                bottom += this.getPaddingBottom();

                range = (range < bottom)? bottom: range;
            }

            // 覆盖整个View
            range = (range < this.getHeight())? this.getHeight(): range;

            int height = mFrameDrawable.getBounds().height();
            if (height != range) {
                mFrameDrawable.setBounds(0, 0, width, range);
            }
        }

        {

            int offset = this.computeVerticalScrollOffset();
            int dy = -offset;

            canvas.save();
            canvas.translate(0, dy);

            mFrameDrawable.draw(canvas);

            canvas.restore();
        }
    }

    void drawBackground(Canvas canvas) {

        if (mBackground == null) {
            return;
        }

        if (mBackgroundSizeChanged) {
            mBackgroundSizeChanged = false;

            int width = this.getWidth();

            int range = this.computeVerticalScrollRange();
            range += mDisplayHeight;

            int height = mBackground.getBounds().height();
            if (height != range) {
                mBackground.setBounds(0, 0, width, range);
            }
        }

        {
            int offset = this.computeVerticalScrollOffset();
            int dy = -offset;

            canvas.save();
            canvas.translate(0, dy);

            mBackground.draw(canvas);

            canvas.restore();
        }
    }

    View findTargetChild(MotionEvent ev) {

        int count = this.getChildCount();
        if (count == 0) {
            return null;
        }

        // 最后一个
        {
            View view = this.getChildAt(count - 1);
            mTempRect.set(0, 0, view.getWidth(), view.getHeight());

            offsetDescendantRectToMyCoords(view, mTempRect);

            float y = ev.getY();
            int bottom = mTempRect.bottom;
            if (y > bottom) {
                return view;
            }
        }

        // 第一个
        {
            View view = this.getChildAt(0);
            mTempRect.set(0, 0, view.getWidth(), view.getHeight());

            offsetDescendantRectToMyCoords(view, mTempRect);

            float y = ev.getY();
            int top = mTempRect.top;
            if (y < top) {
                return view;
            }
        }

        return null;
    }

    /**
     *
     */
    public interface OnNestedScrollListener {

        boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY);

        boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed);

    }
}
