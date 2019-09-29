package com.haiyunshan.pudding.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import com.haiyunshan.pudding.color.ColorPanel;

import java.util.Arrays;

import static android.support.v4.widget.ViewDragHelper.INVALID_POINTER;

public class ColorPlateView extends View {

    int[][] mColors;

    int mBorderWidth = 2;
    int mHeightExtra = 6;

    int[] mCellWidths;

    int mCellWidth;
    int mCellHeight;
    Rect[][] mRects;
    Rect mRect;

    Rect mTmpRect = new Rect();

    Paint mPaint;

    int mColor;

    private int mTouchSlop;
    private boolean mIsBeingDragged = false;
    private int mLastMotionY;
    private int mLastMotionX;
    private int mActivePointerId = INVALID_POINTER;

    OnColorPlateListener mOnColorPlateListener;

    public ColorPlateView(Context context) {
        this(context, null);
    }

    public ColorPlateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPlateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ColorPlateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setClickable(true);

        this.mColor = Color.TRANSPARENT;

        this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mRect = new Rect();

        this.mColors = ColorPanel.instance().getPlate();
        if (mColors != null && mColors.length != 0) {
            this.mCellWidths = new int[mColors[0].length];

            this.mRects = new Rect[mColors.length][];
            for (int i = 0; i < mColors.length; i++) {
                int[] a = mColors[i];
                mRects[i] = new Rect[a.length];
                for (int j = 0; j < a.length; j++) {
                    mRects[i][j] = new Rect();
                }
            }
        }
    }

    public void setOnColorPlateListener(OnColorPlateListener listener) {
        this.mOnColorPlateListener = listener;
    }

    public void setColor(int color) {
        this.mColor = color;

        this.invalidate();
    }

    public boolean hasColor(int color) {
        boolean result = false;

        if (mColors == null || mColors.length == 0) {
            return result;
        }

        int length = mColors.length;
        for (int i = 0; i < length; i++) {
            int[] c = mColors[i];
            for (int j = 0; j < c.length; j++) {
                if (c[j] == color) {
                    return true;
                }
            }
        }

        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mColors == null || mColors.length == 0 || mColors[0].length == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int width = MeasureSpec.getSize(widthMeasureSpec);

        int rows = mColors.length;
        int columns = mColors[0].length;

        int remain = width;
        remain -= mBorderWidth * (columns + 1);

        int cellWidth = remain / columns;
        int cellHeight = cellWidth + mHeightExtra;

        int height = rows * cellHeight;
        height += mBorderWidth * (rows + 1);

        {
            this.mCellWidth = cellWidth;
            this.mCellHeight = cellHeight;
        }

        int measuredWidth = width;
        int measuredHeight = height;

        this.setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mCellWidths == null || mCellWidths.length == 0) {
            return;
        }

        {
            Arrays.fill(mCellWidths, mCellWidth);
            int columns = mCellWidths.length;
            int count = w;
            count -= columns * mCellWidth;
            count -= (columns + 1) * mBorderWidth;
            for (int i = 0; i < count; i++) {
                mCellWidths[i] += 1;
            }
        }

        {
            int left = 0;
            int top = 0;

            int right = 0;
            int bottom = 0;

            int rows = mColors.length;
            for (int i = 0; i < rows; i++) {

                int[] r = mColors[i];
                int columns = r.length;
                for (int j = 0; j < columns; j++) {
                    Rect rect = mRects[i][j];

                    right = left;
                    right += mBorderWidth;
                    right += mCellWidths[j];
                    right += mBorderWidth;

                    bottom = top;
                    bottom += mBorderWidth;
                    bottom += mCellHeight;
                    bottom += mBorderWidth;

                    rect.set(left, top, right, bottom);

                    left += mBorderWidth;
                    left += mCellWidths[j];
                }

                left = 0;
                top += mBorderWidth;
                top += mCellHeight;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Paint paint = mPaint;
        paint.setShader(null);

        {
            int rows = mColors.length;
            for (int i = 0; i < rows; i++) {

                int[] r = mColors[i];
                int columns = r.length;
                for (int j = 0; j < columns; j++) {
                    Rect rect = this.mRect;
                    rect.set(mRects[i][j]);
                    rect.inset(mBorderWidth, mBorderWidth);

                    int color = mColors[i][j];
                    color = (color == Color.TRANSPARENT)? 0xffefeff4: color;

                    paint.setColor(color);
                    paint.setStyle(Paint.Style.FILL);

                    canvas.drawRect(rect, paint);
                }

            }
        }

//        mColor = 0xfff6a3a0;
        if (mColor != Color.TRANSPARENT) {
            Rect rect = this.obtainRect(mColor);
            if (rect != null) {
                paint.setColor(Color.WHITE);
                paint.setStyle(Paint.Style.STROKE);
                float width = mBorderWidth * 6;
                paint.setStrokeWidth(width);

                mRect.set(rect);
                rect = mRect;

                if (rect.top == 0) {
                    rect.top += (width / 2);
                }
                if (rect.left == 0) {
                    rect.left += (width / 2);
                }
                if (rect.right == getWidth()) {
                    rect.right -= (width / 2);
                }
                if (rect.bottom == getHeight()) {
                    rect.bottom -= (width / 2);
                }
                canvas.drawRect(rect, paint);

                // 上
                {
                    paint.setStyle(Paint.Style.FILL);

                    Rect r = mTmpRect;
                    r.set(rect);
                    r.inset((int)(width / 2), (int)(width / 2));
                    r.bottom = r.top + mBorderWidth;

                    float x0 = r.left;
                    float y0 = r.bottom;
                    float x1 = r.left;
                    float y1 = r.top;
                    int color0 = Color.TRANSPARENT;
                    int color1 = 0x30000000;
                    Shader.TileMode tile = Shader.TileMode.CLAMP;

                    LinearGradient shader = new LinearGradient(x0, y0, x1, y1, color0, color1, tile);
                    paint.setShader(shader);

                    canvas.drawRect(r, paint);
                }

                // 左
                {
                    paint.setStyle(Paint.Style.FILL);

                    Rect r = mTmpRect;
                    r.set(rect);
                    r.inset((int)(width / 2), (int)(width / 2));
                    r.right = r.left + mBorderWidth;

                    float x0 = r.right;
                    float y0 = r.bottom;
                    float x1 = r.left;
                    float y1 = r.bottom;
                    int color0 = Color.TRANSPARENT;
                    int color1 = 0x30000000;
                    Shader.TileMode tile = Shader.TileMode.CLAMP;

                    LinearGradient shader = new LinearGradient(x0, y0, x1, y1, color0, color1, tile);
                    paint.setShader(shader);

                    canvas.drawRect(r, paint);
                }

                // 下
                {
                    paint.setStyle(Paint.Style.FILL);

                    Rect r = mTmpRect;
                    r.set(rect);
                    r.inset((int)(width / 2), (int)(width / 2));
                    r.top = r.bottom - (mBorderWidth);

                    float x0 = r.left;
                    float y0 = r.top;
                    float x1 = r.left;
                    float y1 = r.bottom;
                    int color0 = Color.TRANSPARENT;
                    int color1 = 0x20000000;
                    Shader.TileMode tile = Shader.TileMode.CLAMP;

                    LinearGradient shader = new LinearGradient(x0, y0, x1, y1, color0, color1, tile);
                    paint.setShader(shader);

                    canvas.drawRect(r, paint);
                }

                // 右
                {
                    paint.setStyle(Paint.Style.FILL);

                    Rect r = mTmpRect;
                    r.set(rect);
                    r.inset((int)(width / 2), (int)(width / 2));
                    r.left = r.right - (mBorderWidth);

                    float x0 = r.left;
                    float y0 = r.top;
                    float x1 = r.right;
                    float y1 = r.top;
                    int color0 = Color.TRANSPARENT;
                    int color1 = 0x20000000;
                    Shader.TileMode tile = Shader.TileMode.CLAMP;

                    LinearGradient shader = new LinearGradient(x0, y0, x1, y1, color0, color1, tile);
                    paint.setShader(shader);

                    canvas.drawRect(r, paint);
                }
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int y = (int) event.getY();
                final int x = (int) event.getX();

                /*
                 * Remember location of down touch.
                 * ACTION_DOWN always refers to pointer index 0.
                 */
                mLastMotionY = y;
                mLastMotionX = x;

                mActivePointerId = event.getPointerId(0);
                mIsBeingDragged = false;

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                /*
                 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
                 * whether the user has moved far enough from his original down touch.
                 */

                /*
                 * Locally do absolute value. mLastMotionY is set to the y value
                 * of the down event.
                 */
                final int activePointerId = mActivePointerId;
                if (activePointerId == INVALID_POINTER) {
                    // If we don't have a valid id, the touch down wasn't on content.
                    break;
                }

                final int pointerIndex = event.findPointerIndex(activePointerId);
                if (pointerIndex == -1) {
                    break;
                }

                final int y = (int) event.getY(pointerIndex);
                final int x = (int) event.getX(pointerIndex);
                final int yDiff = Math.abs(y - mLastMotionY);
                final int xDiff = Math.abs(x - mLastMotionX);
                if (!mIsBeingDragged
                        && (yDiff > mTouchSlop || xDiff > mTouchSlop)) {
                    mIsBeingDragged = true;
                }

                break;
            }

            case MotionEvent.ACTION_UP: {
                if (!mIsBeingDragged) {
                    performClick();

                    int x = (int) (event.getX());
                    int y = (int) (event.getY());
                    Rect r = this.obtainRect(x, y);
                    if (r != null) {
                        int c = this.obtainColor(r);
                        if (c != Color.TRANSPARENT && mColor != c) {
                            this.mColor = c;
                            this.invalidate();

                            if (mOnColorPlateListener != null) {
                                mOnColorPlateListener.onColorChanged(this, c);
                            }
                        }
                    }
                }

                break;
            }
        }

        return true;
    }

    int obtainColor(Rect rect) {

        int rows = mRects.length;
        for (int i = 0; i < rows; i++) {

            Rect[] r = mRects[i];
            int columns = r.length;
            for (int j = 0; j < columns; j++) {
                if (rect == mRects[i][j]) {
                    return mColors[i][j];
                }
            }
        }

        return Color.TRANSPARENT;
    }

    Rect obtainRect(int color) {
        int rows = mRects.length;
        for (int i = 0; i < rows; i++) {

            Rect[] r = mRects[i];
            int columns = r.length;
            for (int j = 0; j < columns; j++) {

                int c = mColors[i][j];
                if (c == color) {
                    Rect rect = (mRects[i][j]);
                    return rect;
                }
            }
        }

        return null;
    }

    Rect obtainRect(int x, int y) {
        int rows = mRects.length;
        for (int i = 0; i < rows; i++) {

            Rect[] r = mRects[i];
            int columns = r.length;
            for (int j = 0; j < columns; j++) {
                Rect rect = (mRects[i][j]);
                if (rect.contains(x, y)) {
                    return rect;
                }
            }
        }

        return null;
    }

    public interface OnColorPlateListener {

        void onColorChanged(ColorPlateView view, int color);

    }
}
