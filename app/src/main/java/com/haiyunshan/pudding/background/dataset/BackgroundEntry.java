package com.haiyunshan.pudding.background.dataset;

import android.graphics.Color;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.haiyunshan.pudding.color.ColorUtils;
import com.haiyunshan.pudding.dataset.BaseEntry;

/**
 *
 */
public class BackgroundEntry extends BaseEntry {

    @SerializedName("name")
    String mName;

    @SerializedName("fg")
    String mForeground;

    @SerializedName("bg")
    String mBackground;

    @SerializedName("uri")
    String mUri;

    transient int mFG = Color.TRANSPARENT;
    transient int mBG = Color.TRANSPARENT;

    public BackgroundEntry(String id) {
        super(id);
    }

    public String getName() {
        return mName;
    }

    public int getForeground() {
        if (mFG != Color.TRANSPARENT) {
            return mFG;
        }

        if (TextUtils.isEmpty(mForeground)) {
            mFG = Color.BLACK;
            return mFG;
        }

        this.mFG = ColorUtils.parseColor(mForeground);
        return mFG;
    }

    public int getBackground() {
        if (mBG != Color.TRANSPARENT) {
            return mBG;
        }

        if (TextUtils.isEmpty(mBackground)) {
            mBG = Color.WHITE;
            return mBG;
        }

        this.mBG = ColorUtils.parseColor(mBackground);
        return mBG;
    }

    public String getUri() {
        if (mUri == null) {
            return "";
        }

        return mUri;
    }


}
