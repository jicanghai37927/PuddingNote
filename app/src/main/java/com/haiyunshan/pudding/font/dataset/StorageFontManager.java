package com.haiyunshan.pudding.font.dataset;

import android.graphics.Typeface;

import com.haiyunshan.pudding.dataset.FileStorage;
import com.haiyunshan.pudding.utils.GsonUtils;
import com.haiyunshan.pudding.utils.PackageUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

public class StorageFontManager {

    FontDataset mDataset;

    File mFile;
    HashMap<String, WeakReference<Typeface>> mTypefaceMap;

    private static StorageFontManager sInstance;

    public static final StorageFontManager instance() {
        if (sInstance == null) {
            File file = FileStorage.getScanTypefaceDataset();

            sInstance = new StorageFontManager(file);
        }

        return sInstance;
    }

    public StorageFontManager() {
        this(null);
    }

    private StorageFontManager(File file) {
        this.mFile = file;
        this.mTypefaceMap = new HashMap<>();

        if (mFile != null) {
            this.mDataset = GsonUtils.read(mFile, FontDataset.class);
            if (mDataset == null) {
                this.mDataset = new FontDataset();
            }
        } else {
            this.mDataset = new FontDataset();
        }
    }

    public FontDataset getDataset() {
        return mDataset;
    }

    public void validate() {
        FontDataset ds = this.mDataset;
        List<FontEntry> list = ds.getList();
        int size = list.size();
        for (int i = size - 1; i >= 0; i--) {
            FontEntry e = list.get(i);
            String path = e.getSource();
            File file = new File(path);

            if (!file.exists()) {
                list.remove(i);
            }
        }
    }

    public Typeface getTypeface(FontEntry entry) {
        if (!entry.isValid() || !entry.isSupport()) {
            return Typeface.DEFAULT;
        }

        String source = entry.getSource();

        WeakReference<Typeface> ref = mTypefaceMap.get(source);
        if (ref != null && ref.get() == null) {
            mTypefaceMap.remove(source);
            ref = null;
        }

        if (ref == null) {
            Typeface tf = null;

            if (PackageUtils.canRead()) {
                File file = new File(source);
                if (file.exists() && file.canRead()) {
                    tf = Typeface.createFromFile(file);
                }
            }

            if (tf != null) {
                ref = new WeakReference<>(tf);
                mTypefaceMap.put(source, ref);
            }
        }

        if (ref == null) {
            return Typeface.DEFAULT;
        }

        return ref.get();
    }

    public void save() {
        if (mFile == null) {
            return;
        }

        if (mDataset != null) {
            GsonUtils.write(mDataset, mFile);
        }
    }
}
