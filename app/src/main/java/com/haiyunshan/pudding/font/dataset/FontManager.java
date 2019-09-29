package com.haiyunshan.pudding.font.dataset;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.haiyunshan.pudding.App;
import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.dataset.FileStorage;
import com.haiyunshan.pudding.utils.GsonUtils;
import com.haiyunshan.pudding.utils.MD5Utils;
import com.haiyunshan.pudding.utils.UUIDUtils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class FontManager {

    private static FontManager sInstance;

    public static final int STATE_NONE          = 0; // 未安装
    public static final int STATE_INSTALLED     = 1; // 已安装
    public static final int STATE_INSTALLING    = 2; // 安装中

    static final int SORT_HIGH      = 1;
    static final int SORT_NORMAL    = 10;
    static final int SORT_LOW       = 20;

    File mFile;
    FontDataset mDataset;

    FontEntry mDefault;
    FontDataset mSystemDs;

    ArrayList<FontEntry> mList;
    HashMap<FontEntry, WeakReference<Typeface>> mFonts;

    Installer mInstaller;
    ArrayList<FontEntry> mInstallList;
    InstallObservable mObservable;

    Paint mPaint;

    public static FontManager getInstance() {
        if (sInstance == null) {
            File file = FileStorage.getFontDataset();
            sInstance = new FontManager(file);
        }

        return sInstance;
    }

    private FontManager(File file) {

        this.mList = new ArrayList<>();

        {
            this.mFile = file;

            if (mFile != null) {
                this.mDataset = GsonUtils.read(mFile, FontDataset.class);
                if (mDataset == null) {
                    mDataset = new FontDataset();
                }
            } else {
                this.mDataset = new FontDataset();
            }

            mDataset.setSort(SORT_NORMAL);
            mDataset.setEditable(true);
        }

        {
            this.mSystemDs = this.createSystemDs();
            mSystemDs.setSort(SORT_LOW);
            mSystemDs.setEditable(false);

            this.mDefault = mSystemDs.obtainByUri("");
            mDefault.setSort(SORT_HIGH);
        }

        {
            this.mFonts = new HashMap<>(7);
        }

        {
            this.mInstallList = new ArrayList<>();
            this.mObservable = new InstallObservable();
        }

    }

    public List<FontEntry> getList() {
        ArrayList<FontEntry> list = this.mList;
        list.clear();

        list.addAll(mSystemDs.getList());
        list.addAll(mDataset.getList());

        return list;
    }

    public FontEntry obtain(String id) {
        if (TextUtils.isEmpty(id)) {
            return mDefault;
        }

        FontEntry entry = mSystemDs.obtain(id);
        if (entry != null) {
            return entry;
        }

        entry = mDataset.obtain(id);
        if (entry == null) {
            entry = mDefault;
        }

        return entry;
    }

    public Typeface getTypeface(String id) {
        FontEntry entry = obtain(id);
        return getTypeface(entry);
    }

    public Typeface getTypeface(FontEntry entry) {
        Typeface font = Typeface.DEFAULT;

        if (entry == null) {
            return font;
        }

        String uri = entry.getUri();
        if (TextUtils.isEmpty(uri)) {

        } else if (uri.equalsIgnoreCase("sans-serif")) {
            font = Typeface.SANS_SERIF;
        } else if (uri.equalsIgnoreCase("serif")) {
            font = Typeface.SERIF;
        } else if (uri.equalsIgnoreCase("monospace")) {
            font = Typeface.MONOSPACE;
        } else {
            font = this.obtainTypeface(entry);
        }

        if (font == null) {
            font = Typeface.DEFAULT;
        }

        return font;
    }

    /**
     *
     * @param tf
     * @param textSize in pixel units
     * @return
     */
    public int getLineHeight(Typeface tf, float textSize) {
        if (mPaint == null) {
            mPaint = new Paint();
        }

        mPaint.setTypeface(tf);
        mPaint.setTextSize(textSize);

        int height = mPaint.getFontMetricsInt(null);
        return height;
    }

    Typeface obtainTypeface(FontEntry key) {
        Typeface tf = null;

        WeakReference<Typeface> ref = mFonts.get(key);
        if (ref != null) {
            tf = ref.get();
        }

        if (tf != null) {
            return tf;
        }

        // 从文件创建
        {
            File destFile = FileStorage.getFont(key.getUri());
            if (destFile.exists()) {
                tf = Typeface.createFromFile(destFile);
            }
        }

        // 添加到缓存
        if (tf != null) {
            mFonts.remove(key);
            ref = new WeakReference<>(tf);
            mFonts.put(key, ref);
        }

        return tf;
    }

    public static void recycle() {
        sInstance = null;
    }

    public void remove(FontEntry entry) {

        boolean result = mDataset.remove(entry);

        // 删除字体文件
        if (result) {
            String uri = entry.getUri();
            entry = mDataset.obtainByUri(uri);
            if (entry == null) {
                File destFile = FileStorage.getFont(uri);
                if (destFile.exists()) {
                    Observable.just(destFile)
                            .map(new Function<File, File>() {
                                @Override
                                public File apply(File file) throws Exception {
                                    file.delete();

                                    return file;
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();

                }
            }
        }

    }

    public void put(String name, String uri, String source, String md5, int lang, long size) {
        FontEntry e = mDataset.obtainBySource(source);
        mDataset.remove(e);

        e = new FontEntry(UUIDUtils.next(), name, uri, source, md5, lang, size);
        e.setSort(SORT_NORMAL);
        e.setEditable(true);

        mDataset.add(e);
    }

    public FontDataset getDataset() {
        return mDataset;
    }

    public void save() {
        if (mFile == null) {
            return;
        }

        if (mDataset != null) {
            GsonUtils.write(mDataset, mFile);
        }
    }

    FontDataset createSystemDs() {
        FontDataset ds = new FontDataset();

        Context context = App.getInstance();
        String title = context.getString(R.string.default_system_font_name);

        String[][] array = new String[][] {
                {"default",     title,          ""},
                {"sans_serif",  "sans-serif",   "sans-serif"},
                {"serif",       "serif",        "serif"},
                {"monospace",   "monospace",    "monospace"}
        };

        for (String[] a : array) {
            String id = a[0];
            String name = a[1];
            String uri = a[2];
            String source = "";
            String md5 = "";
            int lang = 0;
            long size = 0;

            FontEntry e = new FontEntry(id, name, uri, source, md5, lang, size);
            ds.add(e);
        }

        return ds;
    }

    public int getState(FontEntry entry) {
        if (mInstallList.contains(entry)) {
            return STATE_INSTALLING;
        }

        if (mDataset.obtainBySource(entry.getSource()) != null) {
            return STATE_INSTALLED;
        }

        return STATE_NONE;
    }

    public void install(FontEntry entry) {
        if (mInstallList.contains(entry)) {
            return;
        }

        boolean isEmpty = mInstallList.isEmpty();

        mInstallList.add(entry);
        if (isEmpty) {
            runInstall(entry);
        }
    }

    void runInstall(FontEntry entry) {

        this.mInstaller = new Installer(entry);
        Observable.create(mInstaller)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FontEntry>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(FontEntry entry) {
                        mInstallList.remove(entry);

                        mObservable.notifyChanged(entry);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        mInstaller = null;

                        if (!mInstallList.isEmpty()) {
                            runInstall(mInstallList.get(0));
                        }
                    }
                });
    }

    public void registerInstallObserver(@NonNull InstallObserver observer) {
        mObservable.registerObserver(observer);
    }

    public void unregisterInstallObserver(@NonNull InstallObserver observer) {
        mObservable.unregisterObserver(observer);
    }

    public interface InstallObserver {

        void onChanged(FontManager manager, FontEntry entry);

    }

    private class InstallObservable extends android.database.Observable<InstallObserver> {

        public void notifyChanged(FontEntry entry) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChanged(FontManager.this, entry);
            }
        }

    }

    private class Installer implements ObservableOnSubscribe<FontEntry>, Disposable {

        FontEntry mEntry;
        boolean mDisposed;

        Installer(FontEntry entry) {
            this.mEntry = entry;

            this.mDisposed = false;
        }

        @Override
        public void subscribe(ObservableEmitter<FontEntry> emitter) throws Exception {

            // 获取后缀名
            File file = new File(mEntry.getSource());
            String name = file.getName();
            int pos = name.lastIndexOf('.');
            if (pos < 0) {
                emitter.onNext(mEntry);
                emitter.onComplete();
                return;
            }
            String suffix = name.substring(pos).toLowerCase(); // 后缀统一为小写

            if (this.isDisposed()) {
                return;
            }

            // 计算MD5
            String md5 = mEntry.getMD5();
            if (TextUtils.isEmpty(md5)) {
                md5 = MD5Utils.getFileMD5(file);
            }

            if (TextUtils.isEmpty(md5)) {
                emitter.onNext(mEntry);
                emitter.onComplete();
                return;
            }

            mEntry.setMD5(md5);

            if (this.isDisposed()) {
                return;
            }

            // 拷贝文件
            String uri = md5 + suffix + ".font";
            File destFile = FileStorage.getFont(uri);
            if (!destFile.exists()) {
                String tmp = md5 + suffix + ".tmp";
                File tmpFile = FileStorage.getFont(tmp);

                // 拷贝到临时文件
                FileUtils.copyFile(file, tmpFile);

                // 重命名到目标文件
                if (destFile.exists()) {
                    tmpFile.delete();
                } else {
                    tmpFile.renameTo(destFile);
                }
            }

            // 添加数据
            FontManager mgr = FontManager.getInstance();
            mgr.put(mEntry.getName(), uri, mEntry.getSource(), md5, mEntry.getLanguage(), mEntry.getSize());
            mgr.save();

            if (this.isDisposed()) {
                return;
            }

            // 完成
            emitter.onNext(mEntry);
            emitter.onComplete();
        }

        @Override
        public void dispose() {
            this.mDisposed = true;
        }

        @Override
        public boolean isDisposed() {
            return this.mDisposed;
        }
    }
}
