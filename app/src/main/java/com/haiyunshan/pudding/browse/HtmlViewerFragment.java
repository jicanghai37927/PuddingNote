package com.haiyunshan.pudding.browse;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.html.BaseDiv;
import com.haiyunshan.pudding.html.HtmlImporter;
import com.haiyunshan.pudding.html.HtmlPage;
import com.haiyunshan.pudding.html.ParagraphDiv;
import com.haiyunshan.pudding.html.PictureDiv;
import com.haiyunshan.pudding.note.dataset.NoteEntry;
import com.haiyunshan.pudding.note.dataset.NoteManager;
import com.haiyunshan.pudding.utils.PackageUtils;
import com.haiyunshan.pudding.utils.UUIDUtils;
import com.haiyunshan.pudding.utils.WidgetUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class HtmlViewerFragment extends Fragment implements View.OnClickListener {

    static final int ITEM_PARAGRAPH = 101;
    static final int ITEM_PICTURE   = 102;

    RecyclerView mRecyclerView;
    HtmlAdapter mAdapter;

    Toolbar mToolbar;

    View mActionLayout;
    TextView mImportBtn;

    File mTargetFile;

    HtmlPage mHtmlPage;
    Disposable mDisposable;

    HtmlImporter mImportTask;

    public static final HtmlViewerFragment newInstance(Uri uri) {
        HtmlViewerFragment f = new HtmlViewerFragment();

        if (uri != null) {
            Bundle args = new Bundle();
            args.putString("uri", uri.toString());

            f.setArguments(args);
        }

        return f;
    }

    public HtmlViewerFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_text_viewer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.mRecyclerView = view.findViewById(R.id.recycler_page_view);

            LinearLayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(layout);
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

        {
            this.mActionLayout = view.findViewById(R.id.action_layout);
            mActionLayout.setVisibility(View.VISIBLE);

            this.mImportBtn = view.findViewById(R.id.tv_action);
            mImportBtn.setOnClickListener(this);
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
            String name = "测试/tt/tt.html";
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
                        .subscribe(new Consumer<HtmlPage>() {
                            @Override
                            public void accept(HtmlPage page) throws Exception {
                                onNext(page);
                            }
                        });
    }

    void onNext(HtmlPage page) {
        this.mHtmlPage = page;
        this.mDisposable = null;

        this.mAdapter = new HtmlAdapter();
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onClick(View v) {
        if (v == mImportBtn) {
            this.importFile(mTargetFile);
        }
    }

    void importFile(File file) {

        if (mImportTask != null) {
            return;
        }

        String source = Uri.fromFile(file).toString();

        NoteManager manager = NoteManager.instance();
        final List<NoteEntry> list = manager.obtainBySource(source);
        if (list.size() > 0) {
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) { // 替换
                        int size = list.size();
                        NoteEntry entry = list.get(size - 1);

                        importFile(entry.getId());

                    } else if (which == DialogInterface.BUTTON_NEGATIVE) { // 取消

                    } else if (which == DialogInterface.BUTTON_NEUTRAL) { // 保留两者

                        importFile("");
                    }

                    dialog.dismiss();
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(R.string.import_confirm_msg);
            builder.setPositiveButton(R.string.import_btn_yes, listener);
            builder.setNegativeButton(R.string.import_btn_no, listener);
            builder.setNeutralButton(R.string.import_btn_keep, listener);
            builder.setCancelable(true);

            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);

            dialog.show();
        } else {
            this.importFile("");
        }
    }

    void importFile(String id) {

        if (mImportTask != null) {
            return;
        }

        mImportBtn.setEnabled(false);
        mImportBtn.setText(R.string.import_action_msg);

        if (TextUtils.isEmpty(id)) {
            id = UUIDUtils.next();
        }

        NoteEntry entry = NoteManager.instance().put(id);
        this.mImportTask = new HtmlImporter(mHtmlPage, entry);

        Observable.create(mImportTask)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HtmlImporter>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HtmlImporter htmlImporter) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        onImportComplete();
                    }
                });
    }

    void onImportComplete() {
        mImportTask = null;

        if (this.isDetached() || this.isRemoving()) {
            return;
        }

        mImportBtn.setEnabled(false);
        mActionLayout.setVisibility(View.GONE);

        // Snackbar
        Snackbar bar = Snackbar.make(mRecyclerView, R.string.import_result, Snackbar.LENGTH_LONG);
//        bar.setAction(R.string.btn_edit, new View.OnButtonClickListener() {
//            @Override
//            public void onButtonClick(View v) {
//
//            }
//        });
//        bar.setActionTextColor(getResources().getColor(R.color.primary_color));

        bar.show();

    }

    private class HtmlAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public int getItemViewType(int position) {
            int type = ITEM_PARAGRAPH;
            BaseDiv div = mHtmlPage.get(position);
            if (div instanceof PictureDiv) {
                type = ITEM_PICTURE;
            }

            return type;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder = null;

            LayoutInflater inflater = getActivity().getLayoutInflater();
            switch (viewType) {
                case ITEM_PARAGRAPH: {
                    int resource = R.layout.layout_html_paragraph_item;
                    View view = inflater.inflate(resource, parent, false);
                    holder = new ParagraphHolder(view);

                    break;
                }
                case ITEM_PICTURE: {
                    int resource = R.layout.layout_html_picture_item;
                    View view = inflater.inflate(resource, parent, false);
                    holder = new PictureHolder(view);

                    break;
                }
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            BaseDiv div = mHtmlPage.get(position);
            if (holder instanceof ParagraphHolder) {

                ((ParagraphHolder)holder).bind(position, (ParagraphDiv)div);

            } else if (holder instanceof PictureHolder) {

                ((PictureHolder)holder).bind(position, (PictureDiv) div);

            }
        }

        @Override
        public int getItemCount() {
            return mHtmlPage.size();
        }
    }

    /**
     *
     */
    private class ParagraphHolder extends RecyclerView.ViewHolder {

        TextView mParagraphView;

        public ParagraphHolder(View itemView) {
            super(itemView);

            this.mParagraphView = itemView.findViewById(R.id.tv_paragraph);
        }

        void bind(int position, ParagraphDiv entry) {
//        mParagraphView.setText(entry.getText());

            mParagraphView.setText(entry.getPrettyContent(false, false));

            WidgetUtils.prepareCursorControllers(mParagraphView);
        }
    }

    /**
     *
     */
    private class PictureHolder extends RecyclerView.ViewHolder {

        ImageView mPictureView;
        View mContainer;

        PictureDiv mItem;

        public PictureHolder(View itemView) {
            super(itemView);

            this.mPictureView = itemView.findViewById(R.id.iv_picture);
            this.mContainer = itemView.findViewById(R.id.picture_container);
        }

        void bind(int position, PictureDiv entry) {
            this.mItem = entry;

            this.measure(entry);

            Uri uri = entry.getUri();
            if (uri == null) {
                mPictureView.setImageDrawable(null);
            } else {

                Log.w("AA", "uri = " + uri);

                Context context = itemView.getContext();

                RequestOptions options = new RequestOptions();
                options.placeholder(new ColorDrawable(Color.TRANSPARENT));

                int duration = context.getResources().getInteger(R.integer.composer_picture_fade_duration);

                Glide.with(context)
                        .load(uri)
                        .transition(DrawableTransitionOptions.withCrossFade(duration))
                        .apply(options)
                        .into(mPictureView);
            }
        }

        void measure(PictureDiv entry) {
            ViewGroup.LayoutParams params = mPictureView.getLayoutParams();

            int width = entry.getWidth();
            int height = entry.getHeight();
            if (width < 0) { // 未知图片大小，例如网络图片

                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            } else if (width == 0) { // 图片不存在

                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            } else {
                int[] size = getViewSize(true);

                params.width = size[0];
                params.height = size[1];
            }
        }

        int[] getViewSize(boolean scale) {
            int width = mRecyclerView.getMeasuredWidth();
            width -= (itemView.getPaddingLeft() + itemView.getPaddingRight());
            width -= (mContainer.getPaddingLeft() + mContainer.getPaddingRight());

            if (scale) {
                width = (mItem.getWidth() < width) ? mItem.getWidth() : width;
            }

            int height = width * mItem.getHeight() / mItem.getWidth();

            return new int[] { width, height};
        }

    }

    /**
     *
     */
    private class Loader implements ObservableOnSubscribe<HtmlPage> {

        File mFile;

        Loader(File file) {
            this.mFile = file;
        }

        @Override
        public void subscribe(ObservableEmitter<HtmlPage> emitter) throws Exception {

            HtmlPage page = new HtmlPage(mFile);
            page.inflate();

            emitter.onNext(page);
            emitter.onComplete();
        }
    }

}

