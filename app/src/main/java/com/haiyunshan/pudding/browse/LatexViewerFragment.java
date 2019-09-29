package com.haiyunshan.pudding.browse;


import android.Manifest;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haiyunshan.mathjax.MathJaxView;
import com.haiyunshan.mathjax.OnMathJaxRenderListener;
import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.chapter.PlainText;
import com.haiyunshan.pudding.utils.PackageUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class LatexViewerFragment extends Fragment implements OnMathJaxRenderListener {

    MathJaxView mMathView;

    Toolbar mToolbar;

    File mTargetFile;
    Disposable mDisposable;

    public static final LatexViewerFragment newInstance(Uri uri) {
        LatexViewerFragment f = new LatexViewerFragment();

        if (uri != null) {
            Bundle args = new Bundle();
            args.putString("uri", uri.toString());

            f.setArguments(args);
        }

        return f;
    }

    public LatexViewerFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_latex_viewer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.mMathView = view.findViewById(R.id.mjv_formula);
            mMathView.setRenderListener(this);
        }

        {
            this.mToolbar = view.findViewById(R.id.toolbar);
            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String uri = null;
        Bundle args = this.getArguments();
        if (args != null) {
            uri = args.getString("uri");
        }

        File file = null;
        if (!TextUtils.isEmpty(uri)) {
            try {
                file = new File(new URI(uri.toString()));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        if (file == null) {
            String name = "测试/jlatexmath/xzz/example_03.tex";
//            name = "测试/mathml/example_01.mml";
            file = new File(Environment.getExternalStorageDirectory(), name);
        }

        if (file != null) {
            mToolbar.setTitle(file.getName());

            this.mTargetFile = file;
            this.requestRead();
        }

    }

    @Override
    public void onDestroy() {
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }

        super.onDestroy();
    }

    void requestRead() {

        if (PackageUtils.canRead()) {
            beginRead(mTargetFile);
            return;
        }

        RxPermissions rxPermission = new RxPermissions(getActivity());

        rxPermission
                .requestEach(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {

                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) { // 用户已经同意该权限

                            beginRead(mTargetFile);

                        } else if (permission.shouldShowRequestPermissionRationale) { // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框


                            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == DialogInterface.BUTTON_POSITIVE) {
                                        requestRead();
                                    } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                                        getActivity().onBackPressed();
                                    }
                                }
                            };

                            DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    getActivity().onBackPressed();
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setOnCancelListener(cancelListener);
                            builder.setMessage(R.string.chapter_read_msg_1);
                            builder.setPositiveButton(R.string.btn_continue, listener);
                            builder.setNegativeButton(R.string.btn_cancel, listener);
                            AlertDialog dialog = builder.create();
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();


                        } else { // 用户拒绝了该权限，并且选中『不再询问』

                            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == DialogInterface.BUTTON_POSITIVE) {
                                        PackageUtils.showDetailsSettings(getActivity(), getActivity().getPackageName());

                                        getActivity().onBackPressed();
                                    } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                                        getActivity().onBackPressed();
                                    }
                                }
                            };

                            DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    getActivity().onBackPressed();
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setOnCancelListener(cancelListener);
                            builder.setMessage(R.string.chapter_read_msg_2);
                            builder.setPositiveButton(R.string.btn_setting, listener);
                            builder.setNegativeButton(R.string.btn_cancel, listener);
                            AlertDialog dialog = builder.create();
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();

                        }
                    }
                });
    }

    void beginRead(File file) {
        Loader loader = new Loader(file);

        this.mDisposable =
                Observable.create(loader)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Loader>() {
                            @Override
                            public void accept(Loader loader) throws Exception {
                                onNext(loader);
                            }
                        });
    }

    void onNext(Loader loader) {
        String text = loader.mPlainText.getText();

        mMathView.setText(text);
    }

    @Override
    public void onRendered(MathJaxView view) {
        Log.w("AA", "MathJax render complete");
    }

    /**
     *
     */
    private static class Loader implements ObservableOnSubscribe<Loader> {

        File mFile;

        PlainText mPlainText;

        Loader(File file) {
            this.mFile = file;
        }

        @Override
        public void subscribe(ObservableEmitter<Loader> emitter) throws Exception {

            this.mPlainText = new PlainText(mFile);

            emitter.onNext(this);

            emitter.onComplete();

        }
    }

}
