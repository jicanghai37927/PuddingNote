package com.haiyunshan.pudding.compose.note;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 *
 */
public class PictureEntity extends BaseEntity {

    public static final String TYPE = "picture";

    @SerializedName("uri")
    String mUri;

    @SerializedName("width")
    int mWidth;

    @SerializedName("height")
    int mHeight;

    @SerializedName("desc")
    String mDesc;

    @SerializedName("src")
    String mSource; // uri格式

    public PictureEntity(String id) {
        super(id, TYPE);

        this.mUri = "";
        this.mWidth = 0;
        this.mHeight = 0;
        this.mDesc = "";
        this.mSource = "";
    }

    public String getUri() {
        return mUri;
    }

    public void setUri(String uri) {
        this.mUri = uri;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String text) {
        this.mDesc = text;
    }

    /**
     * uri格式
     *
     * @return
     */
    public String getSource() {
        if (TextUtils.isEmpty(mSource)) {
            return mSource;
        }

        if (mSource.indexOf(':') < 0) {
            return "file://" + mSource;
        }

        return mSource;
    }

    /**
     * uri格式
     *
     * @param path
     */
    public void setSource(String path) {
        if (path.indexOf(':') < 0) {
            path = "file://" + path;
        }

        this.mSource = path;
    }

}
