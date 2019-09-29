package com.haiyunshan.pudding.font;


import android.Manifest;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.font.dataset.FontEntry;
import com.haiyunshan.pudding.font.dataset.FontManager;
import com.haiyunshan.pudding.font.dataset.PreviewDataset;
import com.haiyunshan.pudding.font.dataset.StorageFontManager;
import com.haiyunshan.pudding.font.dataset.StorageFontScanner;
import com.haiyunshan.pudding.utils.GsonUtils;
import com.haiyunshan.pudding.utils.PackageUtils;
import com.haiyunshan.pudding.utils.SoftInputUtils;
import com.haiyunshan.pudding.utils.WindowUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 外部字体浏览
 *
 */
public class FontBookFragment extends Fragment implements Toolbar.OnMenuItemClickListener, View.OnClickListener, ViewPager.OnPageChangeListener, FontManager.InstallObserver {

    static String sPreviewText = null;

    ViewPager mViewPager;
    PreviewAdapter mAdapter;

    Toolbar mToolbar;
    TextView mTitleView;
    TextView mSubtitleView;

    View mBottomBar;
    TextView mActionView;

    StorageFontManager mManager;
    StorageFontScanner mScanner;

    PreviewDataset mPreview;
    boolean mEditPreview;

    File mTargetFile;

    public static final FontBookFragment newInstance(Uri uri) {
        FontBookFragment f = new FontBookFragment();

        Bundle bundle = new Bundle();
        bundle.putString("uri", uri.toString());
        f.setArguments(bundle);

        return f;
    }

    public FontBookFragment() {
        this.mManager = new StorageFontManager();

        this.mEditPreview = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_typeface_preview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.mViewPager = view.findViewById(R.id.view_pager);
            mViewPager.addOnPageChangeListener(this);
            mViewPager.setOffscreenPageLimit(1);

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


            mToolbar.inflateMenu(R.menu.menu_typeface_preview);
            mToolbar.setOnMenuItemClickListener(this);
        }

        {
            view.findViewById(R.id.title_layout).setVisibility(View.VISIBLE);
            this.mTitleView = view.findViewById(R.id.tv_title);
            this.mSubtitleView = view.findViewById(R.id.tv_subtitle);
        }

        {
            this.mBottomBar = view.findViewById(R.id.bottom_bar);

            this.mActionView = view.findViewById(R.id.tv_action);
            mActionView.setOnClickListener(this);

        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mPreview = GsonUtils.readAssets("typeface_preview.json", PreviewDataset.class);

        File file = null;
        Uri uri = Uri.parse(getArguments().getString("uri"));
        try {
            file = new File(new URI(uri.toString()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (file == null) {

        } else {
            this.mTargetFile = file;
        }

        FontManager.getInstance().registerInstallObserver(this);

        {
            ViewTreeObserver observer = mViewPager.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(mLayoutListener);
        }

        this.requestScan();
    }

    @Override
    public void onDestroy() {
        if (mScanner != null) {
            mScanner.dispose();
            mScanner = null;
        }

        FontManager.getInstance().unregisterInstallObserver(this);

        ViewTreeObserver observer = mViewPager.getViewTreeObserver();
        observer.removeOnGlobalLayoutListener(mLayoutListener);

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v == mActionView) {
            int current = mViewPager.getCurrentItem();
            FontEntry entry = mAdapter.mList.get(current);

            FontManager.getInstance().install(entry);

            onPageSelected(current);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        FontEntry entry = mAdapter.mList.get(position);
        String name = entry.getPrettyName();
        String uri = entry.getUri();

        if (TextUtils.isEmpty(name)) {
            if (!entry.isValid()) {
                name = getString(R.string.font_invalid);
            } else if (!entry.isSupport()) {
                name = getString(R.string.font_not_support);
            }
        }

        mTitleView.setText(name);
        mSubtitleView.setText(uri);

        if (!entry.isValid() || !entry.isSupport()) {
            mActionView.setText(R.string.font_action_cannot_install);
            mActionView.setEnabled(false);
        } else {
            int state = FontManager.getInstance().getState(entry);
            if (state == FontManager.STATE_NONE) {
                mActionView.setText(R.string.font_action_none);
                mActionView.setEnabled(true);
            } else if (state == FontManager.STATE_INSTALLING) {
                mActionView.setText(R.string.font_action_installing);
                mActionView.setEnabled(false);
            } else {
                mActionView.setText(R.string.font_action_installed);
                mActionView.setEnabled(false);
            }
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mEditPreview) {
            this.mEditPreview = false;

            mAdapter.setEdit(false);

            MenuItem next = mToolbar.getMenu().findItem(R.id.menu_edit);
            next.setVisible(true);

            MenuItem item = mToolbar.getMenu().findItem(R.id.menu_done);
            item.setVisible(false);
        }
    }

    @Override
    public void onChanged(FontManager manager, FontEntry entry) {
        int current = mViewPager.getCurrentItem();
        FontEntry e = mAdapter.mList.get(current);
        if (entry == e) {
            onPageSelected(current);
        }
    }

    void requestScan() {
        if (PackageUtils.canRead()) {
            beginScan();
            return;
        }

        RxPermissions rxPermission = new RxPermissions(getActivity());

        rxPermission
                .requestEach(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {

                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) { // 用户已经同意该权限

                            beginScan();

                        } else if (permission.shouldShowRequestPermissionRationale) { // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框


                            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == DialogInterface.BUTTON_POSITIVE) {
                                        requestScan();
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
                            builder.setMessage(R.string.font_preview_msg_1);
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
                            builder.setMessage(R.string.font_preview_msg_2);
                            builder.setPositiveButton(R.string.btn_setting, listener);
                            builder.setNegativeButton(R.string.btn_cancel, listener);
                            AlertDialog dialog = builder.create();
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();

                        }
                    }
                });
    }

    void beginScan() {

        File file = this.mTargetFile;
        File folder = file.getParentFile();

        this.mScanner = new StorageFontScanner(mManager, folder);
        mScanner.setFilter(0, false);

        Observable.create(mScanner)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<File>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(File file) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        mScanner = null;

                        buildViewPager();
                    }
                });
    }

    void buildViewPager() {

        mActionView.setVisibility(View.VISIBLE);

        String source = mTargetFile.getAbsolutePath();

        this.mAdapter = new PreviewAdapter(mManager.getDataset().getList());
        mViewPager.setAdapter(mAdapter);
        int pos = mAdapter.indexOf(source);
        if (pos > 0) {
            mViewPager.setCurrentItem(pos);
        }

        pos = mViewPager.getCurrentItem();
        if (pos >= 0) {
            onPageSelected(pos);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_edit: {
                this.mEditPreview = true;

                mAdapter.setEdit(true);

                MenuItem next = mToolbar.getMenu().findItem(R.id.menu_done);
                next.setVisible(true);

                item.setVisible(false);

                break;
            }
            case R.id.menu_done: {
                this.mEditPreview = false;

                mAdapter.setEdit(false);

                MenuItem next = mToolbar.getMenu().findItem(R.id.menu_edit);
                next.setVisible(true);

                item.setVisible(false);

                break;
            }
        }

        return true;
    }

    void setBottomBar(boolean visible) {
        if (visible) {

            if (mBottomBar.getVisibility() != View.VISIBLE) {
                mBottomBar.setVisibility(View.VISIBLE);
            }

        } else {
            if (mBottomBar.getVisibility() == View.VISIBLE) {
                mBottomBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     *
     */
    private class PreviewAdapter extends PagerAdapter {

        List<FontEntry> mList;

        int mPrimaryItem;

        View mCurrentView;

        String mText;

        Collator mCollator;

        PreviewAdapter(List<FontEntry> list) {
            this.mList = list;

            this.mCollator = Collator.getInstance();
            Collections.sort(list, new Comparator<FontEntry>() {
                @Override
                public int compare(FontEntry o1, FontEntry o2) {
                    boolean empty1 = TextUtils.isEmpty(o1.getName());
                    boolean empty2 = TextUtils.isEmpty(o2.getName());
                    if (empty1 && empty2) {
                        return mCollator.compare(o1.getUri(), o2.getUri());
                    } else if (empty1 && !empty2) {
                        return 1;
                    } else if (!empty1 && empty2) {
                        return -1;
                    }

                    return mCollator.compare(o1.getName(), o2.getName());
                }
            });
        }

        int indexOf(String source) {
            for (int i = 0; i < mList.size(); i++) {
                FontEntry e = mList.get(i);
                if (e.getSource().equalsIgnoreCase(source)) {
                    return i;
                }
            }

            return -1;
        }

        void setEdit(boolean value) {
            if (mCurrentView == null) {
                return;
            }

            EditText view = mCurrentView.findViewById(R.id.tv_preview);

            view.setFocusable(value);
            view.setFocusableInTouchMode(value);
            view.setEnabled(value);

            if (value) {
                view.requestFocus();
                view.selectAll();
                SoftInputUtils.show(getActivity(), view);

                this.mText = view.getText().toString();

            } else {
                view.clearFocus();
                view.setSelection(0);
                SoftInputUtils.hide(getActivity(), view);

                if (view.getText().length() == 0) {
                    view.setText(this.mText);
                } else {
                    String text = view.getText().toString();
                    if (!text.equals(mText)) { // 发生过变化，以新的内容为标准
                        sPreviewText = text;

                        this.notifyDataSetChanged();
                    }
                }
            }
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public void startUpdate(@NonNull ViewGroup container) {
            super.startUpdate(container);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            int resource = R.layout.layout_typeface_preview;
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View child = inflater.inflate(resource, container, false);
            container.addView(child);

            EditText view = child.findViewById(R.id.tv_preview);

            FontEntry entry = mList.get(position);
            String text = mPreview.obtain(entry.getLanguage()).mText;
            if (!TextUtils.isEmpty(sPreviewText)) {
                text = sPreviewText;
            }

            StorageFontManager mgr = mManager;
            Typeface tf = mgr.getTypeface(entry);
            view.setTypeface(tf);

            if (!entry.isValid()) {
                view.setText(R.string.font_invalid_file);
            } else if (!entry.isSupport()) {
                view.setText(R.string.font_not_support_file);
            } else {
                view.setText(text);
            }

            {
                view.setFocusable(mEditPreview);
                view.setFocusableInTouchMode(mEditPreview);
                view.setEnabled(mEditPreview);
            }

            return child;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View child = (View)object;
            container.removeView(child);
        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            this.mPrimaryItem = position;
            this.mCurrentView = (View)object;
        }

        @Override
        public void finishUpdate(@NonNull ViewGroup container) {
            super.finishUpdate(container);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return (view == object);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }

    ViewTreeObserver.OnGlobalLayoutListener mLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {

            int displayHeight = WindowUtils.getDisplayHeight(getActivity());
            int decorBottom = WindowUtils.getDecorBottom(getActivity());
            if (decorBottom < displayHeight) {

                setBottomBar(false);

            } else {

                setBottomBar(true);

            }
        }
    };
}
