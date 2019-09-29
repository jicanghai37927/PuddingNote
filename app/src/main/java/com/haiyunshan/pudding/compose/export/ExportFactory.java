package com.haiyunshan.pudding.compose.export;

import android.content.Context;

import com.haiyunshan.pudding.compose.document.Document;

public class ExportFactory {

    public static final int HEXO_MARKDOWN = 1;

    public static final ExportHelper create(Context context, Document document, int type) {
        ExportHelper helper = null;

        if (type == HEXO_MARKDOWN) {
            helper = new ExportHexoMarkdown(context, document);
        }

        return helper;
    }
}
