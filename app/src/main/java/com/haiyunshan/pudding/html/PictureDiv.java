package com.haiyunshan.pudding.html;

import android.net.Uri;
import android.text.TextUtils;

import com.haiyunshan.pudding.compose.document.PictureItem;
import com.haiyunshan.pudding.utils.FileHelper;

import java.io.File;

public class PictureDiv extends BaseDiv {

    Uri mUri;

    String mSource;

    int[] mSize;

    public PictureDiv(HtmlPage page, String source) {
        super(page);

        this.mSource = source;
        this.mUri = toUri(source);
    }

    public Uri getUri() {
        return mUri;
    }

    public int getWidth() {
        if (mSize == null) {
            mSize = getSize();
        }

        return mSize[0];
    }

    public int getHeight() {
        if (mSize == null) {
            mSize = getSize();
        }

        return mSize[1];
    }

    public int[] getSize() {
        Uri uri = this.mUri;
        if (uri == null) {
            return new int[] { 0, 0 };
        }

        String schema = uri.getScheme();
        if (!schema.equalsIgnoreCase("file")) {
            return new int[] { -1, -1 };
        }

        File file = new File(uri.getPath());
        int[] value = PictureItem.getImageWidthHeight(file);
        return value;
    }

    Uri toUri(String source) {
        Uri uri = null;

        String path = source;

        String prefix = null;
        int pos = path.indexOf(':');
        if (pos > 0) {
            prefix = path.substring(0, pos).toLowerCase();
        }

        // 根据schema判断
        if (!TextUtils.isEmpty(prefix)) {
            if (prefix.startsWith("http")) { // http、https
                uri = Uri.parse(path);
            } else if (prefix.startsWith("file")) { // file
                path = Uri.parse(path).getPath();
            }
        }

        // 获取本地文件
        if (uri == null) {
            File file = getFile(path);
            if (file != null) {
                uri = Uri.fromFile(file);
            }
        }

        return uri;
    }

    File getFile(String path) {
        File file;

        while (true) {

            // 绝对路径
            file = new File(path);
            if (file.exists()) {
                return file;
            }

            // 当前目录下
            File folder = mPage.mFile.getParentFile();
            file = new File(folder, path);
            if (file.exists()) {
                return file;
            }

            // 特殊处理macos的RTFD格式
            file = acceptRTFD(mPage.mFile, path);
            if (file != null && file.exists()) {
                return file;
            }

            file = null;
            break;
        }

        return file;
    }

    /**
     * 特殊处理macos的RTFD格式
     *
     * @param file
     * @param source
     * @return
     */
    File acceptRTFD(File file, String source) {
        File f;

        String suffix = ".rtfd";

        String name = FileHelper.getName(file);
        f = new File(file.getParentFile(), name + suffix);
        f = new File(f, source);
        if (f.exists()) {
            return f;
        }

        f = new File(file.getParentFile(), name + suffix.toUpperCase());
        f = new File(f, source);
        if (f.exists()) {
            return f;
        }

        f = null;
        return f;
    }
}
