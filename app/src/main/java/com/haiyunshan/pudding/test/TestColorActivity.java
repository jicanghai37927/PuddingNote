package com.haiyunshan.pudding.test;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.haiyunshan.net.margaritov.preference.colorpicker.ColorPickerView;
import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.background.dataset.BackgroundManager;
import com.haiyunshan.pudding.utils.WidgetUtils;

public class TestColorActivity extends AppCompatActivity {

    EditText mEditText;
    ColorPickerView mPickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_color);

        mEditText = findViewById(R.id.edit_paragraph);
        mEditText.setShowSoftInputOnFocus(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.w("AA", "JUSTIFICATION_MODE_INTER_WORD");
            mEditText.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }

        mPickerView = findViewById(R.id.color_picker_view);
        mPickerView.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                notifyColorChanged(color);
            }
        });

//        mEditText.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);

        mEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateCursor(mEditText);

                WidgetUtils.setCursorDrawableColor(mEditText, Color.RED);
            }
        }, 100);

        findViewById(R.id.btn_minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float value = mEditText.getLineSpacingMultiplier();
                if (value > 0.5f) {
                    value -= 0.1f;
                    mEditText.setLineSpacing(0, value);
                    updateCursor(mEditText);
                }
            }
        });
        findViewById(R.id.btn_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float value = mEditText.getLineSpacingMultiplier();
                if (value < 3.f) {
                    value += 0.1f;
                    mEditText.setLineSpacing(0, value);
                    updateCursor(mEditText);
                }
            }
        });
    }

    void notifyColorChanged(int color) {
        int c = BackgroundManager.getForeground(color);

        mEditText.setBackgroundColor(color);
        mEditText.setTextColor(c);

        mEditText.setHighlightColor(BackgroundManager.getHighlight(c));

        WidgetUtils.setCursorDrawableColor(mEditText, BackgroundManager.getCursorColor(c));
    }

    float getLineHeight(EditText edit) {
        TextPaint paint = edit.getPaint();
        float add = edit.getLineSpacingExtra();
        float mult = edit.getLineSpacingMultiplier();

        float height = paint.getFontMetricsInt(null) * mult + add;
        return height;
    }

    float getTextHeight(EditText edit) {
        TextPaint paint = edit.getPaint();

        float height = paint.getFontMetricsInt(null);
        return height;
    }

    void updateCursor(EditText edit) {
        Drawable[] d = WidgetUtils.getCursorDrawable(edit);
        if (d == null || d.length == 0) {
            return;
        }

        Log.w("AA", "length = " + d.length);

        for (Drawable drawable : d) {
            if (drawable == null) {
                Log.w("AA", "is null");

                continue;
            }

            Log.w("AA", drawable.getClass().toString());

            if (!(drawable instanceof GradientDrawable)) {
                continue;
            }

            GradientDrawable gd = (GradientDrawable)drawable;
            Rect rect = WidgetUtils.getPadding(gd);
            if (rect == null) {
                Log.w("AA", "rect = null");

                continue;
            }

            float offset = getLineHeight(edit) - getTextHeight(edit);
            Log.w("AA", "offset = " + offset);

            offset = -offset;
            rect.bottom = (int)offset;
        }

        edit.setCursorVisible(false);
        edit.setCursorVisible(true);

        edit.invalidate();
    }

}
