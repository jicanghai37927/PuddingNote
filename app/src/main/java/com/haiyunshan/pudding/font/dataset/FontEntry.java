package com.haiyunshan.pudding.font.dataset;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.haiyunshan.pudding.dataset.BaseEntry;
import com.haiyunshan.pudding.utils.UUIDUtils;

public class FontEntry extends BaseEntry {

    @SerializedName("name")
    String mName;   // 名称

    @SerializedName("uri")
    String mUri;    // 资源定位符

    @SerializedName("source")
    String mSource; // 源文件位置

    @SerializedName("md5")
    String mMD5;    // 字体文件MD5

    @SerializedName("lang")
    int mLanguage;  //

    @SerializedName("size")
    long mSize;     // 文件大小

    transient String mPrettyName;   //
    transient int mSort;            //
    transient boolean mEditable;    //

    public FontEntry(String name, String uri, String source, int lang, long size) {
        this(UUIDUtils.next(), name, uri, source, "", lang, size);
    }

    public FontEntry(String id, String name, String uri, String source, String md5, int lang, long size) {
        super(id);

        this.mName = name;
        this.mUri = uri;
        this.mSource = source;
        this.mMD5 = md5;
        this.mLanguage = lang;
        this.mSize = size;
    }

    public String getPrettyName() {
        if (mPrettyName != null) {
            return mPrettyName;
        }

        if (TextUtils.isEmpty(mName)) {
            mPrettyName = "";
            return mPrettyName;
        }

        String name = this.mName;
        int pos = name.lastIndexOf('_');
        if (pos > 0) {
            name = name.substring(0, pos);
        }

        this.mPrettyName = name;
        return name;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getUri() {
        return mUri;
    }

    public void setUri(String uri) {
        this.mUri = uri;
    }

    public String getSource() {
        return mSource;
    }

    public void setSource(String source) {
        this.mSource = source;
    }

    public String getMD5() {
        return mMD5;
    }

    public void setMD5(String md5) {
        this.mMD5 = md5;
    }

    public int getLanguage() {
        return mLanguage;
    }

    public void setLanguage(int lang) {
        this.mLanguage = lang;
    }

    public long getSize() {
        return this.mSize;
    }

    public boolean isValid() {
        if (TextUtils.isEmpty(mName)) {
            return false;
        }

        if (mSize <= 0) {
            return false;
        }

        return true;
    }

    public boolean isSupport() {
        boolean chinese = containChinese(mName);
        if (chinese) {
            long size = mSize;
            return size > 1024 * 1024;
        }

        return true;
    }

    boolean containChinese(String name) {
        int length = name.length();
        for (int i = 0; i < length; i++) {
            char c = name.charAt(i);

            boolean v = (c >= 0x4e00 && c <= 0x9fa5);
            if (v) {
                return true;
            }
        }

        return false;
    }

    public void setSort(int value) {
        this.mSort = value;
    }

    public int getSort() {
        return this.mSort;
    }

    public boolean isEditable() {
        return mEditable;
    }

    public void setEditable(boolean editable) {
        this.mEditable = editable;
    }
}
