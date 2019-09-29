package com.haiyunshan.pudding.color;

import com.google.gson.annotations.SerializedName;
import com.haiyunshan.pudding.utils.GsonUtils;

public final class ColorPanel {

    private static ColorPanel sInstance;

    @SerializedName("color")
    String[][] mColors;

    @SerializedName("plate")
    String[][] mPlate;

    transient int[][] mColorsInt;
    transient int[][] mPlateColors;

    public static final ColorPanel instance() {
        if (sInstance == null) {
            sInstance = create();
        }

        return sInstance;
    }

    public int[][] getColors() {
        if (mColorsInt != null) {
            return mColorsInt;
        }

        this.mColorsInt = getColors(mColors);
        return mColorsInt;
    }

    public int[][] getPlate() {
        if (mPlateColors != null) {
            return mPlateColors;
        }

        this.mPlateColors = getColors(mPlate);
        return mPlateColors;
    }

    static int[][] getColors(String[][] array) {
        int[][] colors = new int[array.length][];
        for (int i = 0; i < colors.length; i++) {
            String[] a = array[i];
            int[] b = new int[a.length];
            for (int j = 0; j < b.length; j++) {
                b[j] = ColorUtils.parseColor(a[j]);
            }

            colors[i] = b;
        }

        return colors;
    }

    static final ColorPanel create() {
        ColorPanel instance = GsonUtils.readAssets("color_panel.json", ColorPanel.class);

        return instance;
    }
}
