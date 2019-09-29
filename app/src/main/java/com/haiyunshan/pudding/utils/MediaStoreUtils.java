package com.haiyunshan.pudding.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.haiyunshan.pudding.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class MediaStoreUtils {

    public static final File write(Context context, String title, int width, int height, File file) {

        // 当前时间
        long current = System.currentTimeMillis();

        // 相册目录
        String name = context.getString(R.string.app_name);
        File screenshotDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), name);
        screenshotDir.mkdirs();

        String fileName = file.getName();
        File target = new File(screenshotDir, fileName);

        // 删除系统图库数据
        deleteMediaStore(context, target.getAbsolutePath());

        // 拷贝一份到指定文件夹
        target.delete();
        try {
            FileUtils.copyFile(file, target);
        } catch (IOException e) {
            e.printStackTrace();
            target = null;
        }

        // 更新MediaStore，显示在相册中
        if (target != null) {

            // 更新系统图库
            updateMediaStore(context,
                    target.getAbsolutePath(),
                    title,
                    current,
                    width, height);
        }

        return target;
    }

    public static final void deleteMediaStore(Context context, String path) {

        try {

            // media provider uses seconds for DATE_MODIFIED and DATE_ADDED, but milliseconds
            // for DATE_TAKEN
            ContentResolver resolver = context.getContentResolver();

            // 删除旧文件
            int count = resolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.ImageColumns.DATA + "=?", new String[] { path});

        } catch (Exception e) {

        }

    }

    /**
     *
     * @param context
     * @param path
     * @param title
     * @param time milliseconds
     * @param width
     * @param height
     */
    public static final void updateMediaStore(Context context,
                                              String path,
                                              String title,
                                              long time,
                                              int width,
                                              int height) {
        try {

            // media provider uses seconds for DATE_MODIFIED and DATE_ADDED, but milliseconds
            // for DATE_TAKEN
            long dateSeconds = time / 1000;

            // mime-type
            String mimeType = "image/png";
            if (!path.toLowerCase().endsWith(".png")) {
                mimeType = "image/jpg";
            }

            // Save the screenshot to the MediaStore
            ContentValues values = new ContentValues();

            values.put(MediaStore.Images.ImageColumns.TITLE, title);
            values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, title);

            values.put(MediaStore.Images.ImageColumns.DATA, path);
            values.put(MediaStore.Images.ImageColumns.MIME_TYPE, mimeType);
            values.put(MediaStore.Images.ImageColumns.WIDTH, width);
            values.put(MediaStore.Images.ImageColumns.HEIGHT, height);

            values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, time);
            values.put(MediaStore.Images.ImageColumns.DATE_ADDED, dateSeconds);
            values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, dateSeconds);

            // 插入
            ContentResolver resolver = context.getContentResolver();
            Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            // 更新
            values.clear();
            values.put(MediaStore.Images.ImageColumns.SIZE, new File(path).length());
            resolver.update(uri, values, null, null);

        } catch (Exception e) {

        }
    }
}
