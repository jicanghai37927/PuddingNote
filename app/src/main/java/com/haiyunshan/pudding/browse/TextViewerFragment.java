package com.haiyunshan.pudding.browse;


import android.Manifest;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.chapter.Chapter;
import com.haiyunshan.pudding.chapter.ChapterBook;
import com.haiyunshan.pudding.chapter.ChapterLayoutManager;
import com.haiyunshan.pudding.chapter.PlainText;
import com.haiyunshan.pudding.utils.PackageUtils;
import com.haiyunshan.pudding.utils.WidgetUtils;
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
public class TextViewerFragment extends Fragment implements Toolbar.OnMenuItemClickListener, View.OnClickListener {

    static final String TAG = "TextViewerFragment";

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    PagerSnapHelper mSnapHelper;

    Toolbar mToolbar;

    View mActionLayout;
    View mImportBtn;

    File mTargetFile;

    ChapterBook mBook;
    Disposable mDisposable;

    int mAdapterSize;  // 当前大小

    ChapterDialogFragment mChapterDialogFragment;

    public static final TextViewerFragment newInstance(Uri uri) {
        TextViewerFragment f = new TextViewerFragment();

        if (uri != null) {
            Bundle args = new Bundle();
            args.putString("uri", uri.toString());

            f.setArguments(args);
        }

        return f;
    }

    public TextViewerFragment() {
        this.mAdapterSize = 0;
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

            mToolbar.inflateMenu(R.menu.menu_text_viewer);
            mToolbar.setOnMenuItemClickListener(this);
        }

        {
            this.mActionLayout = view.findViewById(R.id.action_layout);
            mActionLayout.setVisibility(View.GONE);

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
            String name = "hongloumeng.txt";
            name = "290535.txt";

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
                .subscribe(new Consumer<ChapterBook>() {
                    @Override
                    public void accept(ChapterBook book) throws Exception {
                        onNext(book);
                    }
                });
    }

    void onNext(ChapterBook book) {
        if (book.isDone()) {
            mDisposable = null;
        }

        if (mBook == null) {
            mBook = book;
        }

        if (mAdapter == null) {

            this.buildRecyclerView(book);

        } else {

            int size = book.size();
            if (size != mAdapterSize) {

                int itemCount = (size - mAdapterSize);
                mAdapter.notifyItemRangeInserted(mAdapterSize, itemCount);

                mAdapterSize = size;
            }

        }

        if (book.size() > 1) {
            MenuItem item = mToolbar.getMenu().findItem(R.id.menu_chapter);
            if (!item.isVisible()) {
                item.setVisible(true);
            }
        }

        if (mChapterDialogFragment != null
                && mChapterDialogFragment.isVisible()) {
            mChapterDialogFragment.notifyChapterChanged();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_chapter: {
                this.showChapterDialog();

                break;
            }
        }

        return true;
    }

    void buildRecyclerView(ChapterBook book) {

        mRecyclerView.setPreserveFocusAfterLayout(false);

        if (book.isArbitrary()) {
            LinearLayoutManager layout = new ChapterLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(layout);

            mAdapter = new ArbitraryAdapter();

        } else {
            if (book.isDone() && book.size() <= 1) {
                LinearLayoutManager layout = new ChapterLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                mRecyclerView.setLayoutManager(layout);

                mAdapter = new ArbitraryAdapter();
            } else {
                LinearLayoutManager layout = new ChapterLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                mRecyclerView.setLayoutManager(layout);

                PagerSnapHelper helper = new PagerSnapHelper();
                helper.attachToRecyclerView(mRecyclerView);
                this.mSnapHelper = helper;

                mAdapter = new ChapterAdapter();
            }
        }

        mRecyclerView.setAdapter(mAdapter);
        this.mAdapterSize = book.size();
    }

    void showChapter(int position, Chapter entry) {
        mRecyclerView.scrollToPosition(position);
    }

    void showChapterDialog() {

        FragmentManager fm = this.getChildFragmentManager();
        ChapterDialogFragment f = new ChapterDialogFragment();
        f.show(fm, "chapter");

        this.mChapterDialogFragment = f;
    }

    int getCurrentItem() {
        int pos = 0;

        View child = null;
        if (mSnapHelper != null) {
            child = mSnapHelper.findSnapView(mRecyclerView.getLayoutManager());

        } else {
            int count = mRecyclerView.getChildCount();
            if (count > 0) {
                child = mRecyclerView.getChildAt(0);

            }
        }

        if (child != null) {
            RecyclerView.ViewHolder h = mRecyclerView.findContainingViewHolder(child);
            if (h != null) {
                if (mBook.isArbitrary()) {
                    ArbitraryHolder holder = (ArbitraryHolder) h;
                    pos = holder.mPosition;
                } else {
                    ChapterHolder holder = (ChapterHolder) h;
                    pos = holder.mPosition;
                }

            }
        }

        return pos;
    }

    @Override
    public void onClick(View v) {
        if (v == mImportBtn) {

        }
    }

    /**
     *
     */
    private class ChapterAdapter extends RecyclerView.Adapter<ChapterHolder> {

        @NonNull
        @Override
        public ChapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int resource = R.layout.layout_chapter_preview_item;
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(resource, parent, false);

            ChapterHolder holder = new ChapterHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ChapterHolder holder, int position) {
            Chapter entry = mBook.get(position);
            holder.bind(position, entry);
        }

        @Override
        public int getItemCount() {
            return mBook.size();
        }
    }

    /**
     *
     */
    private class ChapterHolder extends RecyclerView.ViewHolder {

        NestedScrollView mScrollView;
        TextView mContentView;

        int mPosition;

        public ChapterHolder(View itemView) {
            super(itemView);

            this.mScrollView = (NestedScrollView)itemView;
            mScrollView.setSaveEnabled(false);

            this.mContentView = itemView.findViewById(R.id.tv_content);
        }

        void bind(int position, Chapter entry) {
            this.mPosition = position;

//            mContentView.setText(entry.getContent());
            mContentView.setText(entry.getPrettyContent(true, false, false));
            WidgetUtils.nullLayoutsByReflect(mContentView);

            mScrollView.scrollTo(0, 0);
        }
    }


    /**
     *
     */
    private class ArbitraryAdapter extends RecyclerView.Adapter<ArbitraryHolder> {

        @NonNull
        @Override
        public ArbitraryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int resource = R.layout.layout_chapter_arbitrary_item;
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(resource, parent, false);

            ArbitraryHolder holder = new ArbitraryHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ArbitraryHolder holder, int position) {
            Chapter entry = mBook.get(position);
            holder.bind(position, entry);
        }

        @Override
        public int getItemCount() {
            return mBook.size();
        }
    }

    /**
     *
     */
    private class ArbitraryHolder extends RecyclerView.ViewHolder {

        TextView mContentView;

        int mPosition;

        public ArbitraryHolder(View itemView) {
            super(itemView);

            this.mContentView = itemView.findViewById(R.id.tv_content);
        }

        void bind(int position, Chapter entry) {
            this.mPosition = position;

//            mContentView.setText(entry.getContent());
            mContentView.setText(entry.getPrettyContent(true, false, false));
            WidgetUtils.nullLayoutsByReflect(mContentView);

        }
    }

    private class Loader implements ObservableOnSubscribe<ChapterBook> {

        File mFile;

        Loader(File file) {
            this.mFile = file;
        }

        @Override
        public void subscribe(ObservableEmitter<ChapterBook> emitter) throws Exception {

            PlainText pt;
            ChapterBook book;

            {
                long time = System.currentTimeMillis();

                {
                    pt = new PlainText(mFile);
                }

                long ellapse = System.currentTimeMillis() - time;
                Log.e(TAG, "[Nothing happen]IMPORTANT: read text = " + ellapse);
            }

            {
                long time = System.currentTimeMillis();

                {
                    book = new ChapterBook(pt.getText(), true);

                    boolean atOnce = false;
                    if (atOnce) {
                        book.inflate();
                    } else {
                        while (!book.isDone()) {
                            book.next();

                            if (book.size() > 1) {
                                emitter.onNext(book);
                            }
                        }
                    }
                }

                long ellapse = System.currentTimeMillis() - time;
                Log.e(TAG, "[Nothing happen]IMPORTANT: read chapter = " + ellapse);
            }

            emitter.onNext(book);
            emitter.onComplete();
        }
    }

}
