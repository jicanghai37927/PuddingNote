package com.haiyunshan.pudding.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.haiyunshan.pudding.R;

public class FormatTitleBar extends FrameLayout implements View.OnClickListener {

    public static final int BUTTON_CLOSE    = 1;
    public static final int BUTTON_ADD      = 2;
    public static final int BUTTON_EDIT     = 3;
    public static final int BUTTON_DONE     = 4;
    public static final int BUTTON_BACK     = 5;

    View mNormalBar;
    View mBackBtn;
    View mEditBtn;
    TextView mTitleNormal;

    View mEditBar;
    TextView mTitleEdit;

    OnButtonClickListener mOnClickListener;

    public FormatTitleBar(@NonNull Context context) {
        this(context, null);
    }

    public FormatTitleBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FormatTitleBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public FormatTitleBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.setClickable(true);
        this.setHapticFeedbackEnabled(false);
        this.setSoundEffectsEnabled(false);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.merge_format_title_bar, this, true);

        {
            View view = findViewById(R.id.normal_bar);
            this.mNormalBar = view;

            view.findViewById(R.id.btn_back).setOnClickListener(this);
            view.findViewById(R.id.btn_edit).setOnClickListener(this);
            view.findViewById(R.id.btn_close).setOnClickListener(this);

            this.mBackBtn = view.findViewById(R.id.btn_back);
            this.mEditBtn = view.findViewById(R.id.btn_edit);
            this.mTitleNormal = view.findViewById(R.id.tv_title);
        }

        {
            View view = findViewById(R.id.edit_bar);
            this.mEditBar = view;

            view.findViewById(R.id.btn_add).setOnClickListener(this);
            view.findViewById(R.id.btn_done).setOnClickListener(this);
            view.findViewById(R.id.btn_close).setOnClickListener(this);

            this.mTitleEdit = view.findViewById(R.id.tv_title);
        }

    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.mOnClickListener = listener;
    }

    public boolean isEdit() {
        return (mEditBar.getVisibility() == View.VISIBLE);
    }

    public void setEdit(boolean value) {
        if (value) {
            mNormalBar.setVisibility(View.INVISIBLE);

            mEditBar.setVisibility(View.VISIBLE);
        } else {
            mNormalBar.setVisibility(View.VISIBLE);

            mEditBar.setVisibility(View.INVISIBLE);
        }
    }

    public void setEditable(boolean value) {
        int visibility = (value)? View.VISIBLE: View.GONE;
        mEditBtn.setVisibility(visibility);
        mEditBtn.setEnabled(value);
    }

    public void setBackable(boolean value) {
        int visibility = (value)? View.VISIBLE: View.GONE;
        mBackBtn.setVisibility(visibility);
        mBackBtn.setEnabled(value);
    }

    public void setTitle(CharSequence text) {
        mTitleNormal.setText(text);
        mTitleEdit.setText(text);
    }

    public void setTitle(int resId) {
        mTitleNormal.setText(resId);
        mTitleEdit.setText(resId);
    }

    @Override
    public void onClick(View v) {
        int button = -1;

        int id = v.getId();
        switch (id) {
            case R.id.btn_back: {
                button = BUTTON_BACK;

                break;
            }
            case R.id.btn_edit: {
                mNormalBar.setVisibility(View.INVISIBLE);
                mEditBar.setVisibility(View.VISIBLE);

                button = BUTTON_EDIT;

                break;
            }
            case R.id.btn_done: {
                mNormalBar.setVisibility(View.VISIBLE);
                mEditBar.setVisibility(View.INVISIBLE);

                button = BUTTON_DONE;

                break;
            }
            case R.id.btn_close: {
                button = BUTTON_CLOSE;

                break;
            }
            case R.id.btn_add: {
                button = BUTTON_ADD;

                break;
            }
        }

        if (mOnClickListener != null && button > 0) {
            mOnClickListener.onButtonClick(this, button);
        }
    }

    public interface OnButtonClickListener {

        void onButtonClick(FormatTitleBar bar, int which);
    }
}
