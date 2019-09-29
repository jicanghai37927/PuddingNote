package com.haiyunshan.pudding.note.dataset;

import com.google.gson.Gson;
import com.haiyunshan.pudding.dataset.FileStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NoteManager {

    private static NoteManager sInstance;

    NoteDataset mDataset;

    public static final NoteManager instance() {
        if (sInstance == null) {
            sInstance = new NoteManager();
        }

        return sInstance;
    }

    private NoteManager() {
        this.mDataset = read();
    }

    public NoteEntry put(String id) {
        NoteEntry item = this.obtain(id);
        if (item == null) {
            item = new NoteEntry(id);
            mDataset.add(item);
        }

        return item;
    }

    public NoteEntry obtain(String id) {
        NoteEntry item = mDataset.obtain(id);
        return item;
    }

    /**
     * 根据来源获取记录，按创建时间升序
     *
     * @param source
     * @return
     */
    public List<NoteEntry> obtainBySource(String source) {
        ArrayList<NoteEntry> list = new ArrayList<>();

        {
            List<NoteEntry> array = mDataset.getList();
            for (NoteEntry entry : array) {
                String src = entry.getSource();
                if (src.equalsIgnoreCase(source)) {
                    list.add(entry);
                }
            }
        }

        if (!list.isEmpty() && list.size() > 1) {
            Comparator<NoteEntry> comparator = new Comparator<NoteEntry>() {
                @Override
                public int compare(NoteEntry o1, NoteEntry o2) {
                    long dif = o1.getCreated() - o2.getCreated();

                    if (dif > 0) {
                        return 1;
                    } else if (dif < 0) {
                        return -1;
                    }

                    return 0;

                }
            };

            Collections.sort(list, comparator);
        }

        return list;
    }

    public List<NoteEntry> getList() {
        return mDataset.getList();
    }

    public void save() {

        try {
            File file = FileStorage.getNoteDataset();
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fos, "utf-8");

            Gson gson = new Gson();
            gson.toJson(mDataset, writer);

            writer.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    NoteDataset read() {
        File file = FileStorage.getNoteDataset();
        if (!file.exists()) {
            return new NoteDataset();
        }

        NoteDataset ds = null;

        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(fis, "utf-8");

            Gson gson = new Gson();
            ds = gson.fromJson(reader, NoteDataset.class);

            reader.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ds == null) {
            ds = new NoteDataset();
        }

        return ds;
    }
}
