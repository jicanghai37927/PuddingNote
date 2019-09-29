package com.haiyunshan.pudding.background.dataset;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.TextUtils;

import com.haiyunshan.pudding.App;
import com.haiyunshan.pudding.utils.GsonUtils;

/**
 * 
 */
public class BackgroundManager {

    private static BackgroundManager sInstance;

    BackgroundDataset mColors;
    BackgroundDataset mTextures;

    public static final BackgroundManager instance() {
        if (sInstance == null) {
            sInstance = new BackgroundManager();
        }

        return sInstance;
    }

    public BackgroundDataset getColors() {
        if (mColors != null) {
            return mColors;
        }

        String path = "background/colors.json";
        BackgroundDataset ds = GsonUtils.readAssets(path, BackgroundDataset.class);
        if (ds == null) {
            ds = new BackgroundDataset();
        }

        this.mColors = ds;
        return mColors;
    }

    public BackgroundDataset getTextures() {
        if (mTextures != null) {
            return mTextures;
        }

        String path = "background/textures.json";
        BackgroundDataset ds = GsonUtils.readAssets(path, BackgroundDataset.class);
        if (ds == null) {
            ds = new BackgroundDataset();
        }

        this.mTextures = ds;
        return mTextures;
    }

    public BackgroundEntry obtain(String id) {

        {
            BackgroundDataset ds = this.getTextures();
            BackgroundEntry entry = ds.obtain(id);
            if (entry != null) {
                return entry;
            }
        }

        {
            BackgroundDataset ds = this.getColors();
            BackgroundEntry entry = ds.obtain(id);
            if (entry != null) {
                return entry;
            }
        }

        return null;
    }

    public int getResid(String id) {
        if (TextUtils.isEmpty(id)) {
            return 0;
        }

        BackgroundEntry entry = this.obtain(id);
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

    public static int getHighlight(int fgColor) {
        int c = fgColor;

        java.awt.Color color = new java.awt.Color(c);
        color = color.brighter();
        color = color.brighter();
        color = color.brighter();
        color = new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue(), 0x54);
        c = color.getRGB();


        return c;
    }

    public static int getCursorColor(int fgColor) {
        int c = fgColor;

        java.awt.Color color = new java.awt.Color(c);
        color = color.brighter();
//        color = color.brighter();
//        color = color.brighter();
//        color = new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue(), 0x54);
        c = color.getRGB();


        return c;
    }

    public static int getForeground(int bgColor) {
        int c = getComplementary(bgColor);
//        int c = getContrast(bgColor);

        return c;
    }

    public static final int getBackground(int color) {
        if (color == android.graphics.Color.TRANSPARENT) {
            return 0xffe6e6e6;
        }

        return color;
    }

    /**
     * 对比色
     *
     * @param color
     * @return
     */
    static final int getContrast(int color) {
        float hsv[] = new float[3];

        Color.colorToHSV(color, hsv);

        float hue = hsv[0];
        hue += 120;
        hue = ((int)hue) % 360;

        hsv[0] = hue;
        hsv[1] = 1.f - hsv[1];
        hsv[2] = 1.f - hsv[2];

        int c = Color.HSVToColor(hsv);
        return c;
    }

    /**
     * 互补色
     *
     * @param color
     * @return
     */
    static final int getComplementary(int color) {

        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        r = 0xff - r;
        g = 0xff - g;
        b = 0xff - b;

        int c = Color.rgb(r, g, b);
        return c;
    }
}
