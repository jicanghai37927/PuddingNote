package com.haiyunshan.pudding.compose.format;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.haiyunshan.pudding.dataset.BaseEntry;
import com.haiyunshan.pudding.dataset.FileStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Format extends BaseEntry {

    @SerializedName("paragraph")
    TextFormat mParagraphFmt;       // 段落

    Format(String id) {
        super(id);

        this.mParagraphFmt = new TextFormat();
    }

    public void setFormat(Format format) {
        if (this.mParagraphFmt != null && format.mParagraphFmt != null) {
            this.mParagraphFmt.setFormat(format.mParagraphFmt);
        }
    }

    public TextFormat getParagraph() {
        return mParagraphFmt;
    }

    public static final Format create(String id) {
        File folder = FileStorage.getNote(id);
        if (!folder.exists()) {
            return new Format(id);
        }

        File file = new File(folder, "format.json");
        if (!file.exists()) {
            return new Format(id);
        }

        Format fmt = null;

        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(fis, "utf-8");

            Gson gson = new GsonBuilder().create();
            fmt = gson.fromJson(reader, Format.class);

            reader.close();
            fis.close();
        } catch (Exception e) {

        }

        if (fmt == null) {
            fmt = new Format(id);
        }

        return fmt;
    }

    public void save() {
        File folder = FileStorage.getNote(this.mId);
        folder.mkdirs();

        File file = new File(folder, "format.json");

        try {
            file.delete();
            file.createNewFile();

            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fos, "utf-8");

            Gson gson = new Gson();
            gson.toJson(this, writer);

            writer.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
