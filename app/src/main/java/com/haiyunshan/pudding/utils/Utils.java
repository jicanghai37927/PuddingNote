package com.haiyunshan.pudding.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;

public class Utils {

    public static String getSignature(Context context) {

        try {
            /** 通过包管理器获得指定包名包含签名的包信息 **/
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);

            /******* 通过返回的包信息获得签名数组 *******/
            Signature[] signatures = packageInfo.signatures;

            /******* 循环遍历签名数组拼接应用签名 *******/
            return signatures[0].toCharsString();

            /************** 得到应用签名 **************/
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static final String getExtension(File file) {
        String suffix = getSuffix(file);
        if (TextUtils.isEmpty(suffix)) {
            return suffix;
        }

        return suffix.substring(1);
    }

    /**
     * @param file
     * @return
     */
    public static final String getSuffix(File file) {
        String name = file.getName();
        int pos = name.lastIndexOf('.');
        if (pos < 0) {
            return "";
        }

        return name.substring(pos);
    }

    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String path = null;
        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor = context.getContentResolver().query(contentURI, projection, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();

            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            path = cursor.getString(idx);

            cursor.close();
        }

        return path;
    }


    public static final Uri createImageUri(Context context) {

        String name = "" + System.currentTimeMillis();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, name);
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, name + ".jpg");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        return uri;
    }

    public static final void deleteImageUri(Context context, Uri uri) {
        context.getContentResolver().delete(uri, null, null);

    }

}
