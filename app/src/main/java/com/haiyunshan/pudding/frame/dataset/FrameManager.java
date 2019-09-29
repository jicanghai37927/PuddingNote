package com.haiyunshan.pudding.frame.dataset;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;

import com.haiyunshan.pudding.App;
import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.utils.GsonUtils;

/**
 * 
 */
public class FrameManager {

    private static FrameManager sInstance;

    FrameDataset mDs;

    FrameEntry mDefault;

    ColorMatrixColorFilter mColorMatrixFilter;
    LightingColorFilter mLightingFilter;
    PorterDuffColorFilter mPorterDuffFilter;

    public static final FrameManager instance() {
        if (sInstance == null) {
            sInstance = new FrameManager();
        }

        return sInstance;
    }

    public FrameEntry obtain(String id) {
        if (TextUtils.isEmpty(id)) {
            return getDefault();
        }

        FrameEntry entry = getDataset().obtain(id);
        if (entry == null) {
            entry = getDefault();
        }

        return entry;
    }

    public FrameDataset getDataset() {
        if (mDs != null) {
            return mDs;
        }

        String path = "frame/frames.json";
        FrameDataset ds = GsonUtils.readAssets(path, FrameDataset.class);
        if (ds == null) {
            ds = new FrameDataset();
        }

        this.mDs = ds;
        return mDs;
    }

    public FrameEntry getDefault() {
        if (mDefault != null) {
            return this.mDefault;
        }

        FrameEntry entry = new FrameEntry("");
        String name = App.getInstance().getString(R.string.frame_empty_title);
        entry.setName(name);

        mDefault = entry;
        return mDefault;
    }

    public ColorMatrixColorFilter getColorMatrix() {
        if (mColorMatrixFilter != null) {
            return mColorMatrixFilter;
        }

        float[] colorMatrix = {
                1, 0, 0, 0, 0, //red
                0, 0, 0, 0, 0, //green
                0, 0, 0, 0, 0, //blue
                0, 0, 0, 1, 0 //alpha
        };
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        this.mColorMatrixFilter = filter;

        return mColorMatrixFilter;
    }

    public LightingColorFilter getLighting(int mult, int add) {
        if (mLightingFilter != null) {
            return mLightingFilter;
        }

        LightingColorFilter filter = new LightingColorFilter(mult, add);
        this.mLightingFilter = filter;

        return mLightingFilter;
    }

    public PorterDuffColorFilter getPorterDuff(int color) {
        this.mPorterDuffFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
        return mPorterDuffFilter;
    }

    public static int getColor(int fgColor, int bgColor) {
        float fs = (1 - 0.618f);
        float bs = (1 - fs);

        int fr = Color.red(fgColor);
        int fg = Color.green(fgColor);
        int fb = Color.blue(fgColor);

        int br = Color.red(bgColor);
        int bg = Color.green(bgColor);
        int bb = Color.blue(bgColor);

        int r = (int)(fr * fs + br * bs);
        int g = (int)(fg * fs + bg * bs);
        int b = (int)(fb * fs + bb * bs);

        int c = Color.rgb(r, g, b);

        java.awt.Color color = new java.awt.Color(c, true);
        color = color.darker();
        c = color.getRGB();

        return c;
    }

    public int getResid(String id) {
        FrameDataset ds = this.getDataset();
        FrameEntry entry = ds.obtain(id);
        if (entry == null) {
            return 0;
        }

        int resid = getResid(entry);
        return resid;
    }

    public int getResid(FrameEntry entry) {
        if (entry == null) {
            return 0;
        }

        String uri = entry.getUri();
        if (TextUtils.isEmpty(uri)) {
            return 0;
        }

        Context context = App.getInstance();
        Resources resources = context.getResources();
        int resId = resources.getIdentifier(uri, "drawable", context.getPackageName());
        return resId;
    }
}
