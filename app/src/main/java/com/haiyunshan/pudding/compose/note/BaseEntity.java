package com.haiyunshan.pudding.compose.note;

import com.google.gson.annotations.SerializedName;
import com.haiyunshan.pudding.dataset.BaseEntry;

public class BaseEntity extends BaseEntry {

    @SerializedName("type")
    String mType;

    BaseEntity(String id, String type) {
        super(id);

        this.mType = type;
    }

    public String getType() {
        return mType;
    }

}
