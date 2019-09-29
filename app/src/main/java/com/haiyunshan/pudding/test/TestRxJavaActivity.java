package com.haiyunshan.pudding.test;

import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.TypefaceActivity;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class TestRxJavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_rx_java);

//        scanTypeface(new TypefaceObservable());
//        ReadVideoFileByRxjava(Environment.getExternalStorageDirectory());
//        ReadVideoFileByRxjava();

        Uri uri = getIntent().getData();
        if (uri != null) {
            Log.w("AA", uri.toString());
            Log.w("AA", getIntent().toString());
        }


        TypefaceActivity.start(this);
        this.finish();
    }

    void scanTypeface(TypefaceObservable o) {


        Observable.create(o)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<File>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(File file) {
                        Log.w("AA", file.getAbsolutePath());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.w("AA", "complete");
                    }
                });

    }

    private class TypefaceObservable implements ObservableOnSubscribe<File> {

        void listFile(ObservableEmitter<File> emitter, File file) {
            if (file.isDirectory()) {

                File[] files = file.listFiles();
                for (File f : files) {
                    listFile(emitter, f);
                }
            } else {
                if (file.exists() && file.canRead() && file.getName().toLowerCase().endsWith(".ttf")) {
                    emitter.onNext(file);
                }
            }
        }

        @Override
        public void subscribe(ObservableEmitter<File> emitter) {
            File[] array = new File[] { Environment.getExternalStorageDirectory() };
            for (File file : array) {
                listFile(emitter, file);
            }

            emitter.onComplete();
        }
    }

    /**
     * 递归遍历内存寻找视频文件
     * @param f
     * @return
     */
    private Observable<File> listFiles(final File f){
        if(f.isDirectory()){
            return Observable.fromArray(f.listFiles()).flatMap(new Function<File, ObservableSource<File>>() {
                @Override
                public ObservableSource<File> apply(File file) {
                    /**如果是文件夹就递归**/
                    return listFiles(file);
                }
            });
        } else {
            /**是视频文件就通知观察者**/
            if (f.exists() && f.canRead() && f.getName().toLowerCase().endsWith(".ttf")) {
                return Observable.just(f);
            }else{
                /**非视频文件就返回null**/
                return null;
            }
        }
    }
    /**
     * 调用读取视频文件
     * rootFile 可以为内存卡根目录
     */
    private void ReadVideoFileByRxjava(File rootFile) {
        Observable.just(rootFile)
                .flatMap(new Function<File, ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(File file) {
                        return listFiles(file);
                    }
                })
                .subscribe(
                        new Observer<File>() {


                            @Override
                            public void onError(Throwable e) {
                            }

                            @Override
                            public void onComplete() {
                                Log.w("danxx", "onCompleted");
                            }

                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.w("AA", "onSubscribe");
                            }

                            @Override
                            public void onNext(File file) {
                                Log.w("AA", file.getAbsolutePath());
                            }
                        }
                );
    }

    private void ReadVideoFileByRxjava() {
        File rootFile = Environment.getExternalStorageDirectory();

        Observable.just(rootFile)
                .flatMap(new Function<File, ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(File file) {
                        return RxUtil.listFiles(file);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Observer<File>() {

                            @Override
                            public void onError(Throwable e) {
                            }

                            @Override
                            public void onComplete() {
                                Log.d("danxx", "onCompleted");
                            }

                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(File file) {
                                String name = file.getAbsolutePath();

                                Log.d("danxx", "name--->" + name);
                            }
                        }
                );
    }

    private static class RxUtil {

        /**
         * rxjava递归查询内存中的视频文件
         * @param f
         * @return
         */
        public static Observable<File> listFiles(final File f) {

            if(f.isDirectory()){
                return Observable.fromArray(f.listFiles()).flatMap(new Function<File, ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(File file) {
                        /**如果是文件夹就递归**/
                        return listFiles(file);
                    }
                });

            } else {

                /**filter操作符过滤视频文件,是视频文件就通知观察者**/
                return Observable.just(f).filter(new Predicate<File>() {

                    @Override
                    public boolean test(File file) {
                        return file.exists() && file.canRead() && file.getName().toLowerCase().endsWith(".ttf");
                    }
                });
            }
        }

    }

}
