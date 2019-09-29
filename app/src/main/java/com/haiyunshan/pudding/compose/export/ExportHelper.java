package com.haiyunshan.pudding.compose.export;

import android.content.Context;

import com.haiyunshan.pudding.compose.document.Document;

import java.io.File;

public abstract class ExportHelper {

    static final String NAME_ID = "._EXPORT_ID.txt";

    File mTargetFolder;

    Document mDocument;
    Context mContext;

    ExportHelper(Context context, Document document) {
        this.mContext = context;

        this.mDocument = document;
    }

    public File getTarget() {
        return mTargetFolder;
    }

    public abstract void export();
}
