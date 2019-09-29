package com.haiyunshan.pudding.compose.note;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ParagraphEntity extends BaseEntity {

    public static final String TYPE = "paragraph";

    @SerializedName("text")
    String mText;

    @SerializedName("spans")
    ArrayList<SpanEntity> mSpans;

    public ParagraphEntity(String id) {
        super(id, TYPE);

        this.mText = "";
        this.mSpans = new ArrayList<>();
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
    }

    public List<SpanEntity> getSpans() {
        if (mSpans == null) {
            mSpans = new ArrayList<>();
        }

        return mSpans;
    }
}
