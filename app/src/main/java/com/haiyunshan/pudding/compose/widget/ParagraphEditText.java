package com.haiyunshan.pudding.compose.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.widget.AppCompatEditText;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.style.LineSpanRender;
import com.haiyunshan.pudding.utils.WidgetUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 *
 */
public class ParagraphEditText extends AppCompatEditText {

    ArrayList<LineSpanRender> mLineSpanRenders;

    public ParagraphEditText(Context context) {
        this(context, null);
    }

    public ParagraphEditText(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public ParagraphEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {

        // 粘贴为纯文本
        if (id == android.R.id.paste) {
            id = android.R.id.pasteAsPlainText;

            // 6.0之前没有pasteAsPlainText
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                id = android.R.id.paste;
            }
        }

        return super.onTextContextMenuItem(id);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.getLayout() == null) {
            assumeLayoutByReflect();
        }

        if (getText() instanceof Spanned) {
            if (mLineSpanRenders != null) {
                for (LineSpanRender r : mLineSpanRenders) {
                    r.draw(canvas);
                }
            }
        }

        super.onDraw(canvas);
    }

    public void addRender(LineSpanRender r) {
        if (mLineSpanRenders == null) {
            mLineSpanRenders = new ArrayList<>();
        }

        mLineSpanRenders.add(r);
    }

    public void prepareCursorControllers() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { // Android 6.0之前，不需要
//            return;
//        }

        boolean result = this.nullLayoutsByReflect();
        if (!result) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE);
            }
        }
    }

    public boolean nullLayoutsByReflect() {
        TextView view = this;

        try {
            Method textCanBeSelected = TextView.class.getDeclaredMethod("nullLayouts");
            textCanBeSelected.setAccessible(true);
            textCanBeSelected.invoke(view);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean assumeLayoutByReflect() {
        TextView view = this;

        try {
            Method textCanBeSelected = TextView.class.getDeclaredMethod("assumeLayout");
            textCanBeSelected.setAccessible(true);
            textCanBeSelected.invoke(view);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public Drawable[] getCursorDrawableByReflect() {
        EditText editText = this;
        Drawable[] drawables = null;

        try {
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(editText);

            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);

            Object cursorDrawable = fCursorDrawable.get(editor);
            drawables = (Drawable[])cursorDrawable;

        } catch (Exception e) {

        }

        return drawables;
    }

    public void setCursorDrawableColorByReflect(int color) {
        EditText editText = this;

        try {
            Field fCursorDrawableRes =
                    TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(editText);

            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(editText);
            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);

            Drawable[] drawables = new Drawable[2];
            Resources res = editText.getContext().getResources();
            drawables[0] = res.getDrawable(mCursorDrawableRes, null);
            drawables[1] = res.getDrawable(mCursorDrawableRes, null);
            drawables[0].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            drawables[1].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            fCursorDrawable.set(editor, drawables);

        } catch (final Throwable ignored) {
        }
    }

    public void fitCursor() {
        EditText edit = this;
        Drawable[] d = WidgetUtils.getCursorDrawable(edit);
        if (d == null || d.length == 0) {
            return;
        }

        float textHeight;
        float lineHeight;

        {
            TextPaint paint = edit.getPaint();

            float add = edit.getLineSpacingExtra();
            float mult = edit.getLineSpacingMultiplier();

            textHeight = paint.getFontMetricsInt(null);
            lineHeight = textHeight * mult + add;
        }

        for (Drawable drawable : d) {
            if (drawable == null) {
                continue;
            }

            if (!(drawable instanceof GradientDrawable)) {
                continue;
            }

            GradientDrawable gd = (GradientDrawable)drawable;
            Rect rect = WidgetUtils.getPadding(gd);
            if (rect == null) {
                continue;
            }

            float offset = lineHeight - textHeight;

            offset = -offset;
            rect.bottom = (int)offset;
        }
    }
}
