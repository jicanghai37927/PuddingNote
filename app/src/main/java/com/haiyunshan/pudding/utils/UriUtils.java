package com.haiyunshan.pudding.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;

public class UriUtils {

    static final String AUTHORITY = "com.haiyunshan.pudding.fileprovider";

    public static final Uri fromFile(Context context, File file) {
        Uri data;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(context, AUTHORITY, file);
            Log.w("AA", data.toString());
        } else {
            data = Uri.fromFile(file);
        }

        return data;
    }
}
