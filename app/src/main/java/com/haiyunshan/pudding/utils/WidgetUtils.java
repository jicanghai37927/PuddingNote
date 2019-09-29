package com.haiyunshan.pudding.utils;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.Layout;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class WidgetUtils {

    public static void prepareCursorControllers(TextView view) {

        boolean result = nullLayoutsByReflect(view);
        if (!result) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view.setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE);
            }
        }

    }

    public static boolean nullLayoutsByReflect(TextView view) {
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

    public static Drawable[] getCursorDrawable(EditText editText) {
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


    public static Rect getPadding(GradientDrawable drawable) {
        Rect rect = null;

        try {
            Field fEditor = GradientDrawable.class.getDeclaredField("mPadding");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(drawable);

            rect = (Rect)editor;
        } catch (Exception e) {

        }

        return rect;
    }

    public static void setCursorDrawableColor(EditText editText, int color) {
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
}
