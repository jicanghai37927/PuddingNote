package com.haiyunshan.pudding.acknowledge;

import com.google.gson.annotations.SerializedName;
import com.haiyunshan.pudding.utils.GsonUtils;

import java.util.List;

public class Acknowledge {

    static final String pathApps = "acknowledge/apps.json";
    static final String pathOpenSource = "acknowledge/open_source.json";

    public static final AppDataset getApps() {

        AppDataset ds = GsonUtils.readAssets(pathApps, AppDataset.class);
        return ds;
    }

    public static final OpenSourceDataset getOpenSource() {
        OpenSourceDataset ds = GsonUtils.readAssets(pathOpenSource, OpenSourceDataset.class);
        return ds;
    }

    public static class AppDataset {

        @SerializedName("list")
        public List<AppEntry> mList;
    }

    public static class AppEntry {

        @SerializedName("id")
        public String mId;

        @SerializedName("name")
        public String mName;

        @SerializedName("slogan")
        public String mSlogan;
    }

    public static class OpenSourceDataset {

        @SerializedName("list")
        public List<OpenSourceEntry> mList;

    }

    public static class OpenSourceEntry {

        @SerializedName("name")
        public String mName;

        @SerializedName("developer")
        public String mDeveloper;

        @SerializedName("desc")
        public String mDesc;

        @SerializedName("uri")
        public String mUri;

    }
}
