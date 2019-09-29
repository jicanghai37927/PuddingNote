package com.haiyunshan.pudding.font.dataset;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PreviewDataset {

    @SerializedName("list")
    public ArrayList<PreviewEntry> mList;

    public PreviewEntry obtain(int lang) {
        for (PreviewEntry e : mList) {
            if (e.mLanguage == lang) {
                return e;
            }
        }

        return mList.get(0);
    }

    public static class PreviewEntry {

        @SerializedName("lang")
        public int mLanguage;

        @SerializedName("text")
        public String mText;
    }
}
