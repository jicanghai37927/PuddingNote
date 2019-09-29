package com.haiyunshan.pudding.compose.helper;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.haiyunshan.pudding.compose.ComposeFragment;
import com.haiyunshan.pudding.utils.SoftInputUtils;
import com.haiyunshan.pudding.utils.WindowUtils;


public class SoftInputHelper implements View.OnTouchListener {

    static final int DURATION_DELAY = 100;

    ComposeFragment mParentFragment;

    int mDisplayHeight;
    int mDecorBottom;

    boolean mSoftInputVisible;

    Handler mHandler;
    boolean mHideRequest;

    boolean mKeyboardBound = true;  // 进入键盘区域即隐藏

    OnSoftInputListener mOnSoftInputListener;

    public SoftInputHelper(ComposeFragment fragment) {
        this.mParentFragment = fragment;
        this.mHandler = fragment.getHandler();

        this.mDisplayHeight = WindowUtils.getDisplayHeight(fragment.getActivity());

        this.mKeyboardBound = true;
    }

    public void setOnSoftInputListener(OnSoftInputListener listener) {
        this.mOnSoftInputListener = listener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        final int actionMasked = event.getActionMasked();
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN: {

                // 判断软键盘是否显示
                int decorBottom = this.getDecorBottom(mParentFragment.getActivity());
                this.mSoftInputVisible = (decorBottom < mDisplayHeight);
                this.mDecorBottom = decorBottom;

                // 处理底部3个按钮隐藏的情况，全面屏时代
                if (mDisplayHeight < decorBottom) {
                    mDisplayHeight = decorBottom;
                }

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (mSoftInputVisible) {

                    int height = (mDisplayHeight - mDecorBottom);
                    height = height / 3;

                    height = (mKeyboardBound)? 0: height;

                    float y = event.getY();
                    y -= (v.getHeight());
                    if (y > height) {

                        if (!mHideRequest) {
                            int delay = DURATION_DELAY;
                            delay = (mKeyboardBound)? 0: delay;

                            mHandler.postDelayed(mHideRunnable, delay);
                            this.mHideRequest = true;
                        }

                    } else {

                        if (mHideRequest) {
                            mHandler.removeCallbacks(mHideRunnable);

                            mHideRequest = false;
                        }

                    }
                }

                break;
            }

            case MotionEvent.ACTION_UP: {

                if (mHideRequest) {
                    mHandler.removeCallbacks(mHideRunnable);

                    mHideRequest = false;
                }

                break;
            }

            case MotionEvent.ACTION_CANCEL: {

                if (mHideRequest) {
                    mHandler.removeCallbacks(mHideRunnable);

                    mHideRequest = false;
                }

                break;
            }
        }

        return false;
    }

    int getDecorBottom(Activity context) {

        Rect r = new Rect();
        View decorView = context.getWindow().getDecorView();
        decorView.getWindowVisibleDisplayFrame(r);

        return r.bottom;
    }

    Runnable mHideRunnable = new Runnable() {

        @Override
        public void run() {
            SoftInputUtils.hide(mParentFragment.getActivity());

            mSoftInputVisible = false;
            mHideRequest = false;

            if (mOnSoftInputListener != null) {
                mOnSoftInputListener.onSoftInputChanged(SoftInputHelper.this, true, false);
            }
        }

    };

    public static interface OnSoftInputListener {

        void onSoftInputChanged(SoftInputHelper helper, boolean oldValue, boolean newValue);

    }
}
