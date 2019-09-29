package com.haiyunshan.pudding.compose.helper;

import com.haiyunshan.pudding.compose.document.PictureItem;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PictureHelper {

    static final int ACTION_COPY    = 1;
    static final int ACTION_DELETE  = 2;

    public void add(PictureItem item) {
        File dest = item.getTarget();
        if (dest.exists()) {
            return;
        }

        PictureTask task = new PictureTask(ACTION_COPY, item);
        Observable.create(task)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void remove(PictureItem item) {
        File dest = item.getTarget();
        if (!dest.exists()) {
            return;
        }

        PictureTask task = new PictureTask(ACTION_DELETE, item);
        Observable.create(task)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    private class PictureTask implements ObservableOnSubscribe<PictureItem> {

        int mAction;
        PictureItem mItem;

        PictureTask(int action, PictureItem item) {
            this.mAction = action;

            this.mItem = item;
        }

        @Override
        public void subscribe(ObservableEmitter<PictureItem> emitter) {
            if (mAction == ACTION_COPY) {
                copy(emitter);
            } else if (mAction == ACTION_DELETE) {
                delete(emitter);
            }
        }

        void copy(ObservableEmitter<PictureItem> emitter) {

            File src = new File(mItem.getSource());
            File dest = mItem.getTarget();
            File tmp = new File(dest.getAbsolutePath() + ".tmp");

            try {
                FileUtils.copyFile(src, tmp);
                tmp.renameTo(dest);
            } catch (IOException e) {
                e.printStackTrace();
            }

            emitter.onNext(mItem);
            emitter.onComplete();
        }

        void delete(ObservableEmitter<PictureItem> emitter) {

            File file = mItem.getTarget();

            try {
                FileUtils.forceDelete(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            emitter.onNext(mItem);
            emitter.onComplete();
        }
    }
}
