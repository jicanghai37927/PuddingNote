package com.haiyunshan.pudding.compose.export;

import android.content.Context;
import android.text.TextUtils;

import com.android.providers.contacts.HanziToPinyin;
import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.compose.document.BaseItem;
import com.haiyunshan.pudding.compose.document.Document;
import com.haiyunshan.pudding.compose.document.ParagraphItem;
import com.haiyunshan.pudding.compose.document.PictureItem;
import com.haiyunshan.pudding.dataset.FileStorage;
import com.haiyunshan.pudding.utils.FileHelper;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExportHexoMarkdown extends ExportHelper {

    String mUri;

    ExportHexoMarkdown(Context context, Document document) {
        super(context, document);

        this.mUri = "HEXO";
    }

    @Override
    public void export() {
        this.prepare();

        this.remove();

        this.mkdirs();

        this.write();
    }

    void prepare() {
        this.mTargetFolder = this.getOutputFolder();
    }

    void remove() {
        if (mTargetFolder == null) {
            return;
        }

        if (!mTargetFolder.exists()) {
            return;
        }

        // 删除原来的目录
        try {
            FileHelper.forceDelete(mTargetFolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void mkdirs() {
        if (mTargetFolder == null) {
            return;
        }

        // 创建目录
        mTargetFolder.mkdirs();

        // 写入ID信息
        String name = NAME_ID;
        File f = new File(mTargetFolder, name);
        try {
            FileUtils.writeStringToFile(f, mDocument.getId(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void write() {

        String title = mTargetFolder.getName();
        String fileName = getName(title);

        // 主体MD文件
        {
            StringBuilder text = this.makeText(title, fileName);
            File file = new File(mTargetFolder, fileName + ".md");
            try {
                FileUtils.writeStringToFile(file, text.toString(), "utf-8", false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 资源文件
        {
            int picIndex = 0;
            List<BaseItem> list = mDocument.getBody();
            for (BaseItem bi : list) {

                if (bi instanceof PictureItem) {
                    PictureItem item = ((PictureItem) bi);

                    String suffix = item.getSuffix().toUpperCase();
                    String uri = String.format("IMG_%1$04d", (picIndex + 1)) + suffix;

                    String format = "%1$s/%2$s";
                    String target = String.format(format, fileName, uri);

                    File file = new File(mTargetFolder, target);
                    this.copyPicture(item, file);

                    ++picIndex;
                }
            }
        }
    }

    void copyPicture(PictureItem item, File target) {
        File source = item.getFile();
        if (!source.exists()) {
            return;
        }

        try {
            FileUtils.copyFile(source, target);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    StringBuilder makeText(String title, String fileName) {
        StringBuilder sb = new StringBuilder(5 * 1024);

        // 添加头部信息
        this.appendHeader(sb, title);

        // 添加数据体
        int picIndex = 0;
        List<BaseItem> list = mDocument.getBody();
        for (BaseItem bi : list) {
            if (bi instanceof ParagraphItem) {
                ParagraphItem item = ((ParagraphItem) bi);

                String text = item.getText().toString();
                sb.append(text);

            } else if (bi instanceof PictureItem) {
                PictureItem item = ((PictureItem) bi);

                sb.append('\n');

                this.appendPicture(sb, fileName, item, picIndex++);

                sb.append('\n');
            }
        }

        return sb;
    }

    void appendPicture(StringBuilder sb, String name, PictureItem item, int index) {
        String format = "![%1$s](%2$s/%3$s)";

        String desc = item.getDesc().toString();

        String suffix = item.getSuffix().toUpperCase();
        String uri = String.format("IMG_%1$04d", (index + 1)) + suffix;

        String text = String.format(format, desc, name, uri);
        sb.append(text);
    }

    void appendHeader(StringBuilder sb, String title) {

        String format = "---\n" +
                "title: %1$s\n" +           // 标题
                "date: %2$s\n" +            // 日期，2018-05-14 21:21:08
                "categories: %3$s\n" +      // 分类
                "tags: %4$s\n" +            // 标签
                "---\n";

        String date = getTime(mDocument.getModified());

        sb.append(String.format(format, title, date, "", ""));

    }

    String getTime(long time){

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1 = new Date(time);
        String t1 = format.format(d1);

        return t1;
    }

    File getOutputFolder() {
        File file = getExist();
        if (file != null) {
            return file;
        }

        String title;
        File folder = FileStorage.getDocumentExportFolder(this.mUri);

        // 获取名称
        {
            title = mDocument.getTitle()[0];
            if (TextUtils.isEmpty(title)) {
                title = mContext.getString(R.string.export_title);
            } else {
                if (title.length() > 64) {
                    title = title.substring(0, 64);
                }
            }
        }

        // 默认名称
        {
            String name = title;

            file = new File(folder, name);
            if (!file.exists()) {
                return file;
            }
        }

        // 带索引名称
        {
            int index = 1;
            while (true) {

                String name = mContext.getString(R.string.export_title_fmt, title, index);

                file = new File(folder, name);
                if (!file.exists()) {
                    break;
                }

                index++;
            }
        }

        return file;
    }

    File getExist() {
        File folder = FileStorage.getDocumentExportFolder(mUri);
        File[] array = folder.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });

        if (array == null || array.length == 0) {
            return null;
        }

        for (File f : array) {
            folder = accept(f);
            if (folder != null) {
                return folder;
            }
        }

        return null;
    }

    File accept(File file) {
        String name = NAME_ID;
        File f = new File(file, name);
        if (!f.exists()) {
            return null;
        }

        String id = null;
        try {
            id = FileUtils.readFileToString(f, "UTF-8");
            id = id.trim();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(id)) {
            return null;
        }

        String targetId = mDocument.getId();
        boolean result = targetId.equals(id);
        if (!result) {
            return null;
        }

        return file;
    }

    public static String getName(String title) {

        HanziToPinyin pinyin = HanziToPinyin.getInstance();
        ArrayList<HanziToPinyin.Token> list = pinyin.get(title);
        int size = list.size();

        StringBuilder sb = new StringBuilder(2 * size + 1);

        for (HanziToPinyin.Token t : list) {
            String target = t.target;
            if (TextUtils.isEmpty(target)) {
                continue;
            }

            int type = t.type;
            if (type == HanziToPinyin.Token.LATIN) {
                char c = target.charAt(0);
                if (c == '\n' || c == '\r') {
                    continue;
                }

                if (c >= '0' && c <= '9') {
                    sb.append(target);
                } else {
                    String s = target.substring(0, 1).toUpperCase();
                    sb.append(s);
                }

//                sb.append('_');

            } else if (type == HanziToPinyin.Token.PINYIN) {

                String s = target.substring(0, 1).toLowerCase();
                sb.append(s);

//                sb.append('_');


            } else {
                char c = target.charAt(0);
                if (c == '\u3000') {
                    continue;
                }

                sb.append(c);

//                sb.append('_');

            }
        }

        // 删除末尾的_
        if (sb.length() > 0
                && sb.charAt(sb.length() - 1) == '_'
                && title.charAt(title.length() - 1) != '_') {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

}
