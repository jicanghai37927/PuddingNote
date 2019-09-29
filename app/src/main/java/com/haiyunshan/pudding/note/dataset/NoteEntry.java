package com.haiyunshan.pudding.note.dataset;

import com.google.gson.annotations.SerializedName;
import com.haiyunshan.pudding.dataset.BaseEntry;

public class NoteEntry extends BaseEntry {

    @SerializedName("title")
    protected String mTitle;

    @SerializedName("subtitle")
    protected String mSubtitle;

    @SerializedName("source")
    protected String mSource;   // uri格式

    public NoteEntry(String id) {
        super(id);
    }

    public NoteEntry(String id, String title, String subtitle) {
        this(id);

        this.mTitle = title;
        this.mSubtitle = subtitle;
    }

    public String getTitle() {
        if (mTitle == null) {
            return "";
        }

        return mTitle;
    }

    public void setTitle(String title) {

        this.mTitle = (title == null)? "": title;
    }

    public String getSubtitle() {
        if (mSubtitle == null) {
            return "";
        }

        return mSubtitle;
    }

    public void setSubtitle(String subtitle) {
        this.mSubtitle = (subtitle == null)? "": subtitle;
    }

    public String getSource() {
        if (mSource == null) {
            return "";
        }

        return mSource;
    }

    public void setSource(String path) {
        if (path.indexOf(':') < 0) {
            path = "file://" + path;
        }

        this.mSource = path;
    }

}
