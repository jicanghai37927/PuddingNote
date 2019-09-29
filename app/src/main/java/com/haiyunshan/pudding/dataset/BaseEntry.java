package com.haiyunshan.pudding.dataset;

import com.google.gson.annotations.SerializedName;

public class BaseEntry {

    @SerializedName("id")
    protected String mId;

    @SerializedName("created")
    protected long mCreated;

    @SerializedName("modified")
    protected long mModified;

    public BaseEntry(String id) {
        this.mId = id;
        this.mCreated = System.currentTimeMillis();
        this.mModified = this.mCreated;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }


    public long getCreated() {
        if (mCreated == 0) {
            mCreated = System.currentTimeMillis();
        }

        return mCreated;
    }

    public void setCreated(long created) {
        this.mCreated = created;
    }

    public long getModified() {
        if (mModified == 0) {
            mModified = this.getCreated();
        }

        return mModified;
    }

    public void setModified(long modified) {
        this.mModified = modified;
    }
}
