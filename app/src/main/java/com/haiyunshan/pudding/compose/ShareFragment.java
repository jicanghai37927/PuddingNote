package com.haiyunshan.pudding.compose;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.background.dataset.BackgroundManager;
import com.haiyunshan.pudding.compose.adapter.ParagraphHolder;
import com.haiyunshan.pudding.compose.document.BaseItem;
import com.haiyunshan.pudding.compose.document.Document;
import com.haiyunshan.pudding.compose.document.DocumentManager;
import com.haiyunshan.pudding.compose.document.ParagraphItem;
import com.haiyunshan.pudding.compose.format.TextFormat;
import com.haiyunshan.pudding.dataset.FileStorage;
import com.haiyunshan.pudding.drawable.FrameDrawable;
import com.haiyunshan.pudding.font.dataset.FontManager;
import com.haiyunshan.pudding.frame.dataset.FrameEntry;
import com.haiyunshan.pudding.frame.dataset.FrameManager;
import com.haiyunshan.pudding.setting.Setting;
import com.haiyunshan.pudding.snapshot.Snapshot;
import com.haiyunshan.pudding.utils.MediaStoreUtils;
import com.haiyunshan.pudding.utils.PackageUtils;
import com.haiyunshan.pudding.utils.UriUtils;
import com.haiyunshan.pudding.utils.WindowUtils;
import com.haiyunshan.pudding.widget.PreviewLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShareFragment extends Fragment implements View.OnClickListener, ShareActionDialogFragment.OnShareActionListener {

    View mContentLayout;
    CardView mCardView;
    PreviewLayout mPreviewLayout;

    View mShareBtn;

    Document mDocument;

    float mScale;
    int mMinHeight;

    File mTargetFile;   // 保存的目标文件
    int mTargetWidth;
    int mTargetHeight;

    public ShareFragment() {
        this.mDocument = null;
        this.mScale = 0.81f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_share, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            Toolbar toolbar = view.findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        }

        {
            this.mContentLayout = view.findViewById(R.id.scroll_content);
            this.mCardView = view.findViewById(R.id.preview_card);
            this.mPreviewLayout = view.findViewById(R.id.layout_preview);
        }

        {
            this.mShareBtn = view.findViewById(R.id.card_share);
            mShareBtn.setOnClickListener(this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            this.mDocument = DocumentManager.instance().getDocument();
            if (mDocument == null) {
                getActivity().finish();
                return;
            }
        }

        {
            int dWidth = WindowUtils.getDisplayWidth();
            int dHeight = WindowUtils.getRealHeight(getActivity());

            float width = dWidth * mScale;
            this.mMinHeight = (int)(width * dHeight / dWidth);
        }

        {
            this.buildContent(mPreviewLayout, mDocument);
            this.addFooter(mPreviewLayout, mDocument);

            boolean fitBackground = false;
            if (fitBackground) {
                int bgColor = mDocument.getFormat().getParagraph().getBackgroundColor();
                if (bgColor != Color.TRANSPARENT) {
                    mContentLayout.setBackgroundColor(bgColor);
                }
            }
        }

        {
            mPreviewLayout.measure(
                    View.MeasureSpec.makeMeasureSpec(WindowUtils.getDisplayWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            int width = mPreviewLayout.getMeasuredWidth();
            int height = mPreviewLayout.getMeasuredHeight();
            height = (height < mMinHeight)? mMinHeight: height;

            {
                ViewGroup.LayoutParams params = mPreviewLayout.getLayoutParams();
                params.width = width;
                params.height = height;

                mPreviewLayout.setScaleX(mScale);
                mPreviewLayout.setScaleY(mScale);
            }

            {
                ViewGroup.LayoutParams params = mCardView.getLayoutParams();
                params.width = (int) (width * mScale);
                params.height = (int) (height * mScale);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mShareBtn) {
            this.showActionDialog();
        }
    }

    @Override
    public void onAction(ShareActionDialogFragment fragment, int action) {
        executeAction(action, null);
    }

    @Override
    public void onAction(ShareActionDialogFragment fragment, ResolveInfo resolveInfo) {
        executeAction(-1, resolveInfo);
    }

    void executeAction(int action, ResolveInfo resolveInfo) {
        if (mTargetFile != null) {
            execute(action, resolveInfo);
            return;
        }

        ShareAction shareAction = new ShareAction(action, resolveInfo);
        PictureTask task = new PictureTask(shareAction);
        Observable.create(task)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ShareAction>() {
                    @Override
                    public void accept(ShareAction shareAction) throws Exception {
                        execute(shareAction.mAction, shareAction.mResolveInfo);
                    }
                });
    }

    void execute(int action, ResolveInfo resolveInfo) {
        File file = mTargetFile;
        if (file == null) {
            return;
        }

        if (resolveInfo != null) {
            sendNote(getActivity(), resolveInfo, file);
        } else {
            switch (action) {
                case ShareActionDialogFragment.ACTION_SAVE_PICTURE: {
                    this.saveToGallery();
                    break;
                }
            }
        }
    }

    void saveToGallery() {

        Context context = getActivity();
        String title = mDocument.getTitle()[0];
        title = (TextUtils.isEmpty(title))? getString(R.string.app_name): title;
        int width = mTargetWidth;
        int height = mTargetHeight;
        File file = mTargetFile;

        final File temp = MediaStoreUtils.write(context, title, width, height, file);
        if (temp == null) {

        } else {

            String msg = getString(R.string.share_slogan_msg_fmt, getString(R.string.app_name));
            Snackbar bar = Snackbar.make(mShareBtn, msg, Snackbar.LENGTH_LONG);
            bar.setAction(getString(R.string.share_slogan_action_view), new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    requestViewFile(v.getContext(), temp);
                }
            });
            bar.show();
        }

    }

    void sendNote(Activity context, ResolveInfo resolveInfo, File file) {

        Intent intent = new Intent();
        boolean supportImage;

        // mime-type
        {
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            supportImage = PackageUtils.checkSupport(context, resolveInfo, intent);
            if (!supportImage) {
                intent.setType("text/plain");
            }
        }

        // component
        {
            String pkg = resolveInfo.activityInfo.packageName;
            String cls = resolveInfo.activityInfo.name;
            ComponentName cn = new ComponentName(pkg, cls);
            intent.setComponent(cn);
        }

        // text
        {
            CharSequence cs = mDocument.getText();
            String text = cs.toString();
            intent.putExtra(Intent.EXTRA_TEXT, text);
        }

        // picture
        if (supportImage) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_SEND)) {

                intent.removeExtra(Intent.EXTRA_STREAM);
                intent.putExtra(Intent.EXTRA_STREAM, UriUtils.fromFile(context, mTargetFile));
            }

            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_SEND_MULTIPLE)) {

                ArrayList<Uri> imageUris = new ArrayList();
                imageUris.add(UriUtils.fromFile(context, mTargetFile)); // Add your image URIs here

                intent.removeExtra(Intent.EXTRA_STREAM);
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            }
        }

        // start
        try {
            context.startActivity(intent);
        } catch (Exception e) {

        }
    }

    void requestViewFile(Context context, File file) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.setDataAndType(UriUtils.fromFile(context, file), "image/*");

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void showActionDialog() {
        FragmentManager fm = this.getChildFragmentManager();
        ShareActionDialogFragment f = new ShareActionDialogFragment();
        f.setOnShareActionListener(this);
        f.show(fm, "share_action");
    }

    File getFile(Document document) {
        File file = FileStorage.getPictureDir();
        file = new File(file, getName(document));

        return file;
    }

    String getName(Document document) {
        StringBuilder sb = new StringBuilder();

        sb.append("Pudding_");
        sb.append(document.getId());
        sb.append(".jpg");

        return sb.toString();
    }

    Bitmap saveSnapshot(View view, File file) {

        Bitmap bitmap = Snapshot.capture(view);
        if (bitmap != null) {
            int quality = 60; // 及格万岁

            {
                int screen = 12;
                int height = WindowUtils.getRealHeight(getActivity());
                int factor = bitmap.getHeight() / height;
                if (factor > screen) {
                    quality -= 3 * (factor - screen);
                }

                quality = (quality < 20)? 20: quality;
            }

            try {
                file.delete();
                file.createNewFile();

                FileOutputStream fos = new FileOutputStream(file);

                Bitmap.CompressFormat fmt = Bitmap.CompressFormat.JPEG;
                bitmap.compress(fmt, quality, fos);

                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();

                bitmap.recycle();
                bitmap = null;
            }
        }

        return bitmap;
    }

    void addFooter(ViewGroup parent, Document document) {
        String fontId = document.getFormat().getParagraph().getFont();
        Typeface tf = FontManager.getInstance().getTypeface(fontId);

        View view = null;

        TextView dateView;
        TextView authorView;
        ImageView iconView;
        TextView nameView;

        int fgColor = document.getFormat().getParagraph().getTextColor();

        {
            int resource = R.layout.layout_share_footer;
            view = getLayoutInflater().inflate(resource, parent, false);

            java.awt.Color c = new java.awt.Color(fgColor);
            c = c.darker();
            c = new java.awt.Color(c.getRed(), c.getGreen(), c.getBlue(), 0x80);

            view.findViewById(R.id.line_left).setBackgroundColor(c.getRGB());
            view.findViewById(R.id.line_right).setBackgroundColor(c.getRGB());

            dateView = view.findViewById(R.id.tv_date);
            authorView = view.findViewById(R.id.tv_author);
            iconView = view.findViewById(R.id.iv_icon);
            nameView = view.findViewById(R.id.tv_name);

            dateView.setTextColor(fgColor);
            authorView.setTextColor(fgColor);
            nameView.setTextColor(fgColor);

            dateView.setTypeface(tf);
            authorView.setTypeface(tf);
            nameView.setTypeface(tf);
        }

        {
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
//            formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

            String text = formatter.format(date);
            dateView.setText(text);
        }

        {
            String text = Setting.instance().getAuthor();
            if (TextUtils.isEmpty(text)) {
                text = getString(R.string.author_none);
            }
            authorView.setText(text);
        }

        {
            iconView.setImageResource(R.drawable.ic_qrcode);
        }

        {
            java.awt.Color c = new java.awt.Color(fgColor);
            c = c.brighter();

            String text = getString(R.string.app_name);
            SpannableString ss = new SpannableString(text);
            int start = 0;
            int end = ss.length();
            int flags = Spanned.SPAN_INCLUSIVE_INCLUSIVE;
            ss.setSpan(new ForegroundColorSpan(c.getRGB()), start, end, flags);
            ss.setSpan(new StyleSpan(Typeface.BOLD), start, end, flags);

            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(getString(R.string.share_edit_by));
            ssb.append(' ');
            ssb.append(ss);

            nameView.setText(ssb);
        }

        if (view != null) {
            parent.addView(view);
        }
    }

    void buildContent(PreviewLayout layout, Document document) {
        this.applyFrame(layout, document);
        this.applyBackground(layout, document);

        List<BaseItem> body = document.getBody();
        for (BaseItem item : body) {
            View view = null;

            if (item instanceof ParagraphItem) {
                view = this.createView(layout, document, (ParagraphItem)item);
            } else {

            }

            if (view != null) {
                layout.addView(view);
            }
        }
    }

    void applyBackground(LinearLayout view, Document document) {
        TextFormat format = mDocument.getFormat().getParagraph();

        // padding
        {
            int vertical = format.getPaddingVertical();
            vertical -= 8;
            vertical = (vertical < 0) ? 0 : vertical;

            vertical = (int) WindowUtils.dp2px(vertical);

            int left = view.getPaddingLeft();
            int right = view.getPaddingRight();
            int top = view.getPaddingTop();
            int bottom = view.getPaddingBottom();

            {
                top = vertical;
                bottom = vertical;

                view.setPadding(left, top, right, bottom);
            }
        }

        // 背景颜色
        {
            int bgColor = format.getBackgroundColor();
            bgColor = BackgroundManager.getBackground(bgColor);
            view.setBackgroundColor(bgColor);
        }

        // 背景纹理
        {
            String textureId = format.getBackgroundTexture();

            int resid = 0;
            if (!TextUtils.isEmpty(textureId)) {
                resid = BackgroundManager.instance().getResid(textureId);
            }

            if (resid != 0) {
                Drawable d = getResources().getDrawable(resid, null);
                view.setBackground(d);
            }
        }
    }

    void applyFrame(PreviewLayout view, Document document) {
        TextFormat format = document.getFormat().getParagraph();

        FrameDrawable frame = view.getFrameDrawable();

        {
            String frameId = format.getFrame();
            FrameEntry entry = FrameManager.instance().obtain(frameId);
            int resid = FrameManager.instance().getResid(entry);
            if (resid == 0) {
                frame.setDrawable(null);
            } else {
                Drawable d = getResources().getDrawable(resid, null);
                int fgColor = format.getTextColor();
                int bgColor = format.getBackgroundColor();
                bgColor = BackgroundManager.getBackground(bgColor);
                int color = FrameManager.getColor(fgColor, bgColor);

                ColorFilter filter = FrameManager.instance().getPorterDuff(color);
                d.setColorFilter(filter);

                FrameEntry.Insets margin = entry.getMargin();
                int top = (int) WindowUtils.dp2px(margin.mTop);
                int left = (int) WindowUtils.dp2px(margin.mLeft);
                int bottom = (int) WindowUtils.dp2px(margin.mBottom);
                int right = (int) WindowUtils.dp2px(margin.mRight);

                frame.setDrawable(d, left, top, right, bottom);
            }
        }
    }

    View createView(LinearLayout parent, Document document, ParagraphItem item) {
        View view = ParagraphHolder.createView(getLayoutInflater(), parent, document, item);
        return view;
    }

    /**
     *
     */
    private class ShareAction {

        int mAction;
        ResolveInfo mResolveInfo;

        ShareAction(int action, ResolveInfo resolveInfo) {
            this.mAction = action;
            this.mResolveInfo = resolveInfo;
        }
    }

    /**
     *
     */
    private class PictureTask implements ObservableOnSubscribe<ShareAction> {

        ShareAction mAction;

        PictureTask(ShareAction action) {
            this.mAction = action;
        }

        @Override
        public void subscribe(ObservableEmitter<ShareAction> emitter) {

            File file = getFile(mDocument);
            Bitmap bitmap = saveSnapshot(mPreviewLayout, file);
            if (bitmap != null) {
                mTargetFile = file;
                mTargetWidth = bitmap.getWidth();
                mTargetHeight = bitmap.getHeight();

                bitmap.recycle();
                bitmap = null;
            }

            emitter.onNext(mAction);
            emitter.onComplete();
        }




    }
}
