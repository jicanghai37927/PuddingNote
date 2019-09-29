package com.haiyunshan.pudding.compose.note;

import com.google.gson.annotations.SerializedName;

public class SpanEntity {

    @SerializedName("type")
    String mType;

    @SerializedName("start")
    int mStart;

    @SerializedName("end")
    int mEnd;

    @SerializedName("params")
    String[] mParams;

    public SpanEntity(String type, int start, int end) {
        this.mType = type;
        this.mStart = start;
        this.mEnd = end;
    }

    public String getType() {
        if (mType == null) {
            return "";
        }

        return mType;
    }

    public int getStart() {
        return mStart;
    }

    public void setStart(int start) {
        this.mStart = start;
    }

    public int getEnd() {
        return mEnd;
    }

    public void setEnd(int end) {
        this.mEnd = end;
    }

    public String[] getParams() {
        return mParams;
    }

    public void setParams(String... params) {
        this.mParams = params;
    }
}
