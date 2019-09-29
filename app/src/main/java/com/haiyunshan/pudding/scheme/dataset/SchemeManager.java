package com.haiyunshan.pudding.scheme.dataset;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.haiyunshan.pudding.App;
import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.font.dataset.FontEntry;
import com.haiyunshan.pudding.font.dataset.FontManager;
import com.haiyunshan.pudding.utils.GsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class SchemeManager {

    private static SchemeManager sInstance;

    SchemeDataset mDs;

    SchemeEntry mDefault;

    public static final SchemeManager instance() {
        if (sInstance == null) {
            sInstance = new SchemeManager();
        }

        return sInstance;
    }

    public SchemeEntry obtain(String id) {
        if (TextUtils.isEmpty(id)) {
            return getDefault();
        }

        SchemeEntry entry = getDataset().obtain(id);
        if (entry == null) {
            entry = getDefault();
        } else {
            if (TextUtils.isEmpty(entry.getFont())) {
                this.applyPrefer(entry);
            }
        }

        return entry;
    }

    public SchemeDataset getDataset() {
        if (mDs != null) {
            return mDs;
        }

        String path = "scheme/schemes.json";
        SchemeDataset ds = GsonUtils.readAssets(path, SchemeDataset.class);
        if (ds == null) {
            ds = new SchemeDataset();
        }

        this.mDs = ds;
        return mDs;
    }

    public SchemeEntry getDefault() {
        if (mDefault != null) {
            return this.mDefault;
        }

        SchemeEntry entry = new SchemeEntry("");

        String name = App.getInstance().getString(R.string.scheme_default_title);
        String text = App.getInstance().getString(R.string.scheme_default_text);

        entry.setName(name);
        entry.setText(text);

        entry.mPrefer = null;

        mDefault = entry;
        return mDefault;
    }

    public void applyPrefer(SchemeEntry entry) {

        if (!TextUtils.isEmpty(entry.mFont)) {
            return;
        }

        SchemeEntry.PreferFont prefer = entry.getPrefer();
        if (prefer == null) {
            return;
        }

        if (TextUtils.isEmpty(prefer.mFontName)) {
            return;
        }

        String fontId = this.queryFont(prefer.mFontName);
        if (TextUtils.isEmpty(fontId)) {
            return;
        }

        {
            entry.mFont = fontId;
            entry.mTextSize = prefer.mTextSize;
            entry.mPaddingLeft = prefer.mPaddingLeft;
            entry.mPaddingRight = prefer.mPaddingRight;
            entry.mPaddingTop = prefer.mPaddingTop;
            entry.mPaddingBottom = prefer.mPaddingBottom;
            entry.mLineMult = prefer.mLineMult;
            entry.mLetterMult = prefer.mLetterMult;
        }
    }

    String queryFont(String fontName) {
        FontEntry entry = null;

        // 同名字体，名称短的胜出
        {
            FontManager mgr = FontManager.getInstance();
            List<FontEntry> all = mgr.getList();
            for (FontEntry e : all) {
                if (e.getPrettyName().indexOf(fontName) >= 0) {
                    if (entry == null) {
                        entry = e;
                    } else {
                        if (entry.getPrettyName().length() > e.getPrettyName().length()) {
                            entry = e;
                        }
                    }
                }
            }
        }

        if (entry == null) {
            return null;
        }

        return entry.getId();
    }

}
