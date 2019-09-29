package com.haiyunshan.pudding.setting;

import com.google.gson.annotations.SerializedName;
import com.haiyunshan.pudding.dataset.FileStorage;
import com.haiyunshan.pudding.utils.GsonUtils;

import java.io.File;

/**
 *
 */
public class Setting {

    @SerializedName("author")
    String mAuthor;

    private static Setting sInstance;

    public static final Setting instance() {
        if (sInstance == null) {
            File file = FileStorage.getSetting();
            sInstance = GsonUtils.read(file, Setting.class);
        }

        if (sInstance == null) {
            sInstance = new Setting();
        }

        return sInstance;
    }

    Setting() {

    }

    public String getAuthor() {
        if (mAuthor == null) {
            return "";
        }

        return mAuthor;
    }

    public void setAuthor(String author) {
        this.mAuthor = author;
    }

    public void save() {
        if (sInstance == null) {
            return;
        }

        Setting ds = sInstance;
        File file = FileStorage.getSetting();
        GsonUtils.write(ds, file);
    }

}
