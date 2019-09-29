package com.haiyunshan.pudding.dataset;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.App;

import java.io.File;

public class FileStorage {

    private static final String PICTURE_NAME    = "Pictures";

    private static final String STORAGE_NAME    = "Pudding";
    private static final String STORAGE_FILES   = "files";

    private static final String NOTE_FOLDER     = "note";
    private static final String NOTE_DATASET    = "note_ds.json";

    private static final String FONT_FOLDER     = "font";
    private static final String FONT_DATASET    = "font_ds.json";

    private static final String SCAN_FOLDER      = "scan";
    private static final String SCAN_TYPEFACE    = "typeface_ds.json";

    private static final String SETTING         = "setting.json";

    /**
     * 导出文档类型目录-URI
     *
     * @param uri
     * @return
     */
    public static final File getDocumentExportFolder(String uri) {
        File file = getDocumentExportRoot();

        file = new File(file, uri);

        file.mkdirs();

        return file;
    }

    /**
     * 导出文档根目录-文档Document
     *
     * @return
     */
    public static final File getDocumentExportRoot() {
        Context context = App.getInstance();

        File file = getExportRoot();

        String name = context.getString(R.string.export_document);
        file = new File(file, name);

        file.mkdirs();

        return file;
    }

    /**
     * 导出数据根目录
     *
     * @return
     */
    public static final File getExportRoot() {
        Context context = App.getInstance();

        File file = Environment.getExternalStorageDirectory();

        String name = context.getString(R.string.export_folder);
        file = new File(file, name);

        file.mkdirs();

        return file;
    }

    public static final File getScanTypefaceDataset() {
        File dir = getStorageDir();
        dir = new File(dir, SCAN_FOLDER);
        dir.mkdirs();

        File file = new File(dir, SCAN_TYPEFACE);
        return file;
    }

    public static final File getFont(String uri) {
        File dir = getStorageDir();
        dir = new File(dir, FONT_FOLDER);
        dir = new File(dir, STORAGE_FILES);
        dir.mkdirs();

        dir = new File(dir, uri);

        return dir;
    }

    /**
     *
     * @return
     */
    public static final File getFontDataset() {
        File dir = getStorageDir();
        dir = new File(dir, FONT_FOLDER);
        dir.mkdirs();

        File file = new File(dir, FONT_DATASET);
        return file;
    }

    public static final File getNotePicture(String noteId, String uri) {
        File file = getNote(noteId);
        file = new File(file, "pictures");
        file.mkdirs();

        file = new File(file, uri + ".picture");
        return file;
    }

    /**
     *
     * @param id
     * @return
     */
    public static final File getNote(String id) {
        File dir = getStorageDir();
        dir = new File(dir, NOTE_FOLDER);
        dir = new File(dir, STORAGE_FILES);
        dir = new File(dir, id + ".note");

        return dir;
    }

    /**
     *
     * @return
     */
    public static final File getNoteDataset() {
        File dir = getStorageDir();
        dir = new File(dir, NOTE_FOLDER);
        dir.mkdirs();

        File file = new File(dir, NOTE_DATASET);
        return file;
    }

    /**
     *
     * @return
     */
    public static final File getSetting() {
        File dir = getStorageDir();
        File file = new File(dir, SETTING);
        
        return file;
    }

    /**
     * 数据保存目录
     *
     * @return
     */
    public static final File getStorageDir() {
        App context = App.getInstance();

        // 直接使用API创建
        File dir = context.getExternalFilesDir(STORAGE_NAME);

        // 创建文件夹方式
        if (!dir.exists()) {
            dir = context.getExternalFilesDir(null);
            dir = new File(dir, STORAGE_NAME);
            dir.mkdirs();
        }

        return dir;
    }

    /**
     * 图片保存目录
     *
     * @return
     */
    public static final File getPictureDir() {
        App context = App.getInstance();

        // 直接使用API创建
        File dir = context.getExternalFilesDir(PICTURE_NAME);

        // 创建文件夹方式
        if (!dir.exists()) {
            dir = context.getExternalFilesDir(null);
            dir = new File(dir, PICTURE_NAME);
            dir.mkdirs();
        }

        return dir;
    }
}
