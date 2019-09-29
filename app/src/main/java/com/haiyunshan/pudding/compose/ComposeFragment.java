package com.haiyunshan.pudding.compose;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.haiyunshan.pudding.ComposeActivity;
import com.haiyunshan.pudding.FtpServerActivity;
import com.haiyunshan.pudding.MoreActivity;
import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.ShareActivity;
import com.haiyunshan.pudding.background.dataset.BackgroundManager;
import com.haiyunshan.pudding.compose.adapter.BaseHolder;
import com.haiyunshan.pudding.compose.adapter.DocumentAdapter;
import com.haiyunshan.pudding.compose.document.BaseItem;
import com.haiyunshan.pudding.compose.document.Document;
import com.haiyunshan.pudding.compose.document.DocumentManager;
import com.haiyunshan.pudding.compose.document.ParagraphItem;
import com.haiyunshan.pudding.compose.document.PictureItem;
import com.haiyunshan.pudding.compose.event.FormatAlignmentEvent;
import com.haiyunshan.pudding.compose.event.FormatBackgroundEvent;
import com.haiyunshan.pudding.compose.event.FormatColorEvent;
import com.haiyunshan.pudding.compose.event.FormatCompleteEvent;
import com.haiyunshan.pudding.compose.event.FormatFontEvent;
import com.haiyunshan.pudding.compose.event.FormatFrameEvent;
import com.haiyunshan.pudding.compose.event.FormatPaddingEvent;
import com.haiyunshan.pudding.compose.event.FormatSchemeEvent;
import com.haiyunshan.pudding.compose.event.FormatSpacingEvent;
import com.haiyunshan.pudding.compose.event.FormatTextSizeEvent;
import com.haiyunshan.pudding.compose.export.ExportDialogFragment;
import com.haiyunshan.pudding.compose.export.ExportFactory;
import com.haiyunshan.pudding.compose.export.ExportHelper;
import com.haiyunshan.pudding.compose.format.TextFormat;
import com.haiyunshan.pudding.compose.helper.SoftInputHelper;
import com.haiyunshan.pudding.compose.note.Note;
import com.haiyunshan.pudding.compose.state.BaseState;
import com.haiyunshan.pudding.compose.state.StateMachine;
import com.haiyunshan.pudding.compose.widget.ComposeRecyclerView;
import com.haiyunshan.pudding.drawable.FrameDrawable;
import com.haiyunshan.pudding.frame.dataset.FrameEntry;
import com.haiyunshan.pudding.frame.dataset.FrameManager;
import com.haiyunshan.pudding.note.dataset.NoteEntry;
import com.haiyunshan.pudding.note.dataset.NoteManager;
import com.haiyunshan.pudding.scheme.dataset.SchemeEntry;
import com.haiyunshan.pudding.utils.FileHelper;
import com.haiyunshan.pudding.utils.SoftInputUtils;
import com.haiyunshan.pudding.utils.Utils;
import com.haiyunshan.pudding.utils.WindowUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

/**
 *
 */
public class ComposeFragment extends Fragment implements Toolbar.OnMenuItemClickListener,
        ComposeRecyclerView.OnNestedScrollListener,
        SoftInputHelper.OnSoftInputListener {

    static final int REQUEST_PHOTO  = 1001;
    static final int REQUEST_CAMERA = 1002;
    static final int REQUEST_MORE   = 1003;

    ComposeRecyclerView mRecyclerView;
    DocumentAdapter mAdapter;

    Toolbar mToolbar;

    SoftInputHelper mSoftInputHelper;

    BottomSheetFragment mBottomSheet;

    Document mDocument;
    Handler mHandler;

    StateMachine mStateMachine;
    Uri mPictureUri;

    public static final ComposeFragment newInstance(Bundle args) {
        ComposeFragment f = new ComposeFragment();

        if (args != null) {
            f.setArguments(args);
        }

        return f;
    }

    public ComposeFragment() {
        this.mHandler = new Handler();

        this.mPictureUri = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.mRecyclerView = view.findViewById(R.id.recycler_list_view);

        this.mToolbar = view.findViewById(R.id.toolbar);

        {
            this.mBottomSheet = (BottomSheetFragment) (getChildFragmentManager().findFragmentById(R.id.composer_bottom_sheet_fragment));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            this.mSoftInputHelper = new SoftInputHelper(this);
        }

        String action = ComposeActivity.ACTION_NOTE;

        {
            Bundle args = this.getArguments();
            String id = (args == null) ? "pudding" : args.getString("id", "pudding");
            Note note = Note.create(id);
            this.mDocument = new Document(note);

            if (args != null) {
                action = args.getString("action", action);
            }
        }

        {
            // 更新背景
            this.applyBackground(false);
            this.applyFrame();
        }

        {
            mRecyclerView.setOnNestedScrollListener(this);
            mRecyclerView.setOnDispatchTouchListener(mSoftInputHelper);

            LinearLayoutManager layout = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(layout);

            this.mAdapter = new DocumentAdapter(this);
            mRecyclerView.setAdapter(mAdapter);
        }

        {
            this.mStateMachine = new StateMachine(this);
            mStateMachine.push(StateMachine.READ);
        }

        {
            if (action.equalsIgnoreCase(ComposeActivity.ACTION_CAMERA)) {
                mStateMachine.push(StateMachine.EDIT);

                this.takePhoto();

            } else if (action.equalsIgnoreCase(ComposeActivity.ACTION_PHOTO)) {
                mStateMachine.push(StateMachine.EDIT);

                this.selectPhoto();

            } else {

                if (mDocument.isEmpty()) {
                    mStateMachine.push(StateMachine.EDIT);
                }
            }
        }

        {
            mStateMachine.peek().onEnter();
        }

        {
            DocumentManager.instance().setDocument(this.mDocument);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                if (mPictureUri != null) {
                    String path = Utils.getRealPathFromURI(getActivity(), mPictureUri);
                    if (!TextUtils.isEmpty(path)) {
                        this.insertPictures(new String[]{path});
                    }

                    mPictureUri = null;
                }
            } else {
                if (mPictureUri != null) {
                    Utils.deleteImageUri(getActivity(), mPictureUri);

                    mPictureUri = null;
                }
            }

        } else if (requestCode == REQUEST_PHOTO) {
            if (resultCode == RESULT_OK) {
                if (data != null) {

                    ArrayList<Uri> list = new ArrayList<>();

                    {
                        ClipData clipData = data.getClipData();
                        if (clipData != null) {
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                ClipData.Item item = clipData.getItemAt(i);
                                Uri uri = item.getUri();
                                list.add(uri);
                            }
                        }

                        if (list.isEmpty()) {
                            Uri uri = data.getData();
                            if (uri != null) {
                                list.add(uri);
                            }
                        }
                    }

                    {
                        String[] array = new String[list.size()];
                        int index = 0;

                        for (Uri uri : list) {
                            String uriString = uri.toString();
                            String path;

                            if (uriString.contains("content")) {
                                path = Utils.getRealPathFromURI(getActivity(), uri);
                            } else {
                                path = uriString.replace("file://", "");
                            }

                            path = (path == null) ? uriString : path;
                            array[index++] = path;
                        }

                        this.insertPictures(array);
                    }
                }
            }
        } else if (requestCode == REQUEST_MORE) {
            if ((resultCode == RESULT_OK) && (data != null)) {
                int event = data.getIntExtra("event", -1);
                switch (event) {
                    case MoreFragment.EVENT_PAGE: {
                        startFormat();

                        break;
                    }
                    case MoreFragment.EVENT_CREATE: {

                        if (this.getActivity() instanceof ComposeActivity) {
                            ComposeActivity context = (ComposeActivity)(this.getActivity());
                            context.createNote(this.mDocument);
                        }

                        break;
                    }
                }
            }


        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        {
            this.saveNote();

            this.updateEntry();
        }

        super.onPause();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);

        {
            mDocument.save();
        }

        super.onStop();
    }

    @Subscribe
    public void onFormatEvent(FormatCompleteEvent event) {
        this.closeFormat();

        mDocument.getFormat().save();
    }

    @Subscribe
    public void onFormatEvent(FormatFontEvent event) {
        String font = event.mFont.getId();

        TextFormat format = mDocument.getFormat().getParagraph();
        format.setFont(font);

        mAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onFormatEvent(FormatTextSizeEvent event) {
        int textSize = event.mTextSize;

        TextFormat format = mDocument.getFormat().getParagraph();
        format.setTextSize(textSize);

        mAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onFormatEvent(FormatColorEvent event) {
        int color = event.mColor;

        TextFormat format = mDocument.getFormat().getParagraph();
        format.setTextColor(color);

        mAdapter.notifyDataSetChanged();

        // 更新边框
        this.updateFrame();

    }

    @Subscribe
    public void onFormatEvent(FormatBackgroundEvent event) {
        int color = event.mFGColor;
        int bgColor = event.mBGColor;
        String textureId = event.mTextureId;

        TextFormat format = mDocument.getFormat().getParagraph();
        format.setTextColor(color);
        format.setBackgroundColor(bgColor);
        format.setBackgroundTexture(textureId);

        mAdapter.notifyDataSetChanged();

        // 更新边框
        this.updateFrame();

        // 更新背景
        this.applyBackground(false);
    }

    @Subscribe
    public void onFormatEvent(FormatPaddingEvent event) {
        TextFormat format = mDocument.getFormat().getParagraph();

        int vertical = event.mPaddingTop;
        int horizontal = event.mPaddingLeft;

        if (vertical >= 0) {
            format.setPaddingVertical(vertical);

            this.applyBackground(true);
        }

        if (horizontal >= 0) {
            format.setPaddingHorizontal(horizontal);

            mAdapter.notifyDataSetChanged();
        }

    }

    @Subscribe
    public void onFormatEvent(FormatAlignmentEvent event) {
        TextFormat format = mDocument.getFormat().getParagraph();
        format.setAlignment(event.mAlign);

        mAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onFormatEvent(FormatSpacingEvent event) {
        TextFormat format = mDocument.getFormat().getParagraph();

        int line = event.mLineSpacing;
        int letter = event.mLetterSpacing;

        if (line > 0) {
            format.setLineSpacingMultiplier(line);
        }

        if (letter > 0) {
            format.setLetterSpacingMultiplier(letter);
        }

        mAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onFormatEvent(FormatFrameEvent event) {
        TextFormat format = mDocument.getFormat().getParagraph();

        boolean isTop = !mRecyclerView.canScrollVertically(-1);

        String frameId = format.getFrame();
        format.setFrame(event.mFrameId);

        if (!frameId.equalsIgnoreCase(event.mFrameId)) {
            this.applyFrame();
        }

        {
            FrameEntry entry = FrameManager.instance().obtain(event.mFrameId);
            FrameEntry.Insets insets = entry.getInsets();

            int horizontal = (insets.mLeft > insets.mRight) ? insets.mLeft : insets.mRight;
            int vertical = (insets.mTop > insets.mBottom) ? insets.mTop : insets.mBottom;

            if (format.getPaddingHorizontal() != horizontal) {
                format.setPaddingHorizontal(horizontal);

                mAdapter.notifyDataSetChanged();
            }

            if (format.getPaddingVertical() != vertical) {
                format.setPaddingVertical(vertical);

                this.applyBackground(false);
            }
        }

        if (isTop) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    int offset = mRecyclerView.computeVerticalScrollOffset();
//                    offset += mRecyclerView.getPaddingTop();
//                    offset += mRecyclerView.getPaddingBottom();

                    mRecyclerView.smoothScrollBy(0, -offset);
                }
            });
        }

    }

    @Subscribe
    public void onFormatEvent(FormatSchemeEvent event) {
        TextFormat format = mDocument.getFormat().getParagraph();

        SchemeEntry entry = event.mEntry;

        {
            format.setScheme(entry.getId());
            format.setScheme(entry);
        }

        {
            mAdapter.notifyDataSetChanged();

            this.applyFrame();

            this.applyBackground(true);
        }
    }

    void applyFrame() {
        TextFormat format = mDocument.getFormat().getParagraph();

        FrameDrawable frame = mRecyclerView.getFrameDrawable();

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

            mRecyclerView.invalidate();
        }
    }

    void updateFrame() {
        FrameDrawable frame = mRecyclerView.getFrameDrawable();
        if (frame == null || frame.getDrawable() == null) {
            return;
        }

        TextFormat format = mDocument.getFormat().getParagraph();

        int fgColor = format.getTextColor();

        int bgColor = format.getBackgroundColor();
        bgColor = BackgroundManager.getBackground(bgColor);

        int color = FrameManager.getColor(fgColor, bgColor);

        ColorFilter filter = FrameManager.instance().getPorterDuff(color);
        frame.setColorFilter(filter);

        mRecyclerView.invalidate();
    }

    void applyBackground(boolean scroll) {
        TextFormat format = mDocument.getFormat().getParagraph();

        {
            int vertical = format.getPaddingVertical();
            vertical -= 8;
            vertical = (vertical < 0) ? 0 : vertical;

            vertical = (int) WindowUtils.dp2px(vertical);

            int left = mRecyclerView.getPaddingLeft();
            int right = mRecyclerView.getPaddingRight();
            int top = mRecyclerView.getPaddingTop();
            int bottom = mRecyclerView.getPaddingBottom();

            if (top != vertical) {
                final boolean isTop = mRecyclerView.canScrollVertically(1);
                final boolean isBottom = mRecyclerView.canScrollVertically(-1);

                final int offsetY = (vertical - top);
                top = vertical;
                bottom = vertical;

                mRecyclerView.setPadding(left, top, right, bottom);
                mRecyclerView.requestLayout();

                if (scroll) {
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (isTop || !isBottom) {
                                if (isTop) {
                                    int offset = mRecyclerView.computeVerticalScrollOffset();
                                    mRecyclerView.smoothScrollBy(0, -offset);
                                } else {
                                    mRecyclerView.smoothScrollBy(0, -offsetY);
                                }
                            } else {
                                mRecyclerView.smoothScrollBy(0, offsetY);
                            }
                        }
                    });

                }
            }
        }

        int bgColor = format.getBackgroundColor();
        bgColor = BackgroundManager.getBackground(bgColor);

        String textureId = format.getBackgroundTexture();

        {
            View view = this.getView();

            view.setBackgroundColor(bgColor);

            mRecyclerView.setBackgroundColor(bgColor);

            int resid = 0;
            if (!TextUtils.isEmpty(textureId)) {
                resid = BackgroundManager.instance().getResid(textureId);
            }

            if (resid == 0) {
                mRecyclerView.setScrollBackground(null);
            } else {
                {
                    Drawable d = getResources().getDrawable(resid, null);
                    mRecyclerView.setScrollBackground(d);
                }

                {
                    Drawable d = getResources().getDrawable(resid, null);
                    view.setBackground(d);
                }

                {
                    Drawable d = getResources().getDrawable(resid, null);
                    mRecyclerView.setBackground(d);
                }

            }
        }
    }

    void updateEntry() {
        NoteEntry entry;

        {
            String id = mDocument.getId();
            NoteManager mgr = NoteManager.instance();
            entry = mgr.obtain(id);
            if (entry == null) {
                entry = mgr.put(id);
            }
        }

        {
            entry.setCreated(mDocument.getCreated());
            entry.setModified(mDocument.getModified());
        }

        {
            String title = null;
            String subtitle = null;

            String[] array = mDocument.getTitle();
            title = array[0];
            subtitle = array[1];

            entry.setTitle(title);
            entry.setSubtitle(subtitle);
        }

        NoteManager.instance().save();
    }

    public Document getDocument() {
        return this.mDocument;
    }

    public StateMachine getStateMachine() {
        return mStateMachine;
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public RecyclerView getRecyclerView() {
        return this.mRecyclerView;
    }

    public BottomSheetFragment getBottomSheet() {
        return mBottomSheet;
    }

    public DocumentAdapter getAdapter() {
        return this.mAdapter;
    }

    public Handler getHandler() {
        return mHandler;
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        if (velocityY < 0 && Math.abs(velocityY) >= 3700) {
            long duration = 5 * 60 * 1000; // XX分钟

            long time = mDocument.getSavedTime();
            long current = System.currentTimeMillis();
            long ellapse = current - time;
            if (ellapse > duration) {
                this.saveNote();
            }
        }

        return false;
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        if (velocityY < 0 && Math.abs(velocityY) >= 3700) {
            SoftInputUtils.hide(getActivity());
        }

        return false;
    }

    void saveDocument() {
        int count = mRecyclerView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mRecyclerView.getChildAt(i);
            RecyclerView.ViewHolder h = mRecyclerView.getChildViewHolder(child);
            BaseHolder holder = (BaseHolder) h;
            holder.onSave();
        }
    }

    public void startMore() {
        MoreActivity.startForResult(this, REQUEST_MORE);
    }

    public void startShare() {
        ShareActivity.start(this);
    }

    public void closeEdit() {
        mStateMachine.pop();

        // 保存笔记
        this.saveNote();
    }

    public void startEdit() {
        mStateMachine.push(StateMachine.EDIT);
    }

    public boolean closeFormat() {
        BaseState state = mStateMachine.pop();

        mDocument.getFormat().save();

        mBottomSheet.reset();

        return (state != null);
    }

    public void startFormat() {
        mStateMachine.push(StateMachine.FORMAT);
    }

    public boolean onBackPressed() {
        boolean result = mStateMachine.peek().onBackPressed();

        return result;
    }

    void saveNote() {
        this.saveDocument();

        mDocument.save();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_take_photo: {
                this.takePhoto();
                break;
            }
            case R.id.menu_picture: {
                this.selectPhoto();
                break;
            }
//            case R.id.menu_export: {
//                this.showExportDialog();
//
//                break;
//            }
        }

        return true;
    }

    void showExportDialog() {

        FragmentManager fm = this.getChildFragmentManager();
        ExportDialogFragment f = new ExportDialogFragment();
        f.show(fm, "export");

    }

    public void export(int type) {

        ExportHelper helper = ExportFactory.create(getActivity(), mDocument, type);

        ExportTask task = new ExportTask(helper);
        Observable.create(task)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ExportHelper>() {
                    @Override
                    public void accept(ExportHelper helper) {
                        onExportComplete(helper);
                    }
                });

    }

    void onExportComplete(final ExportHelper helper) {
        File file = helper.getTarget();

        String[] array = FileHelper.getPrettyPath(getActivity(), file);
        StringBuilder sb = new StringBuilder(128);
        sb.append(getString(R.string.export_msg_prefix));
        for (String str : array) {
            sb.append(str);
        }

        Snackbar bar = Snackbar.make(mRecyclerView, sb, Snackbar.LENGTH_LONG);
        bar.setAction(R.string.btn_ftp, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFtp(helper);
            }
        });
        bar.setActionTextColor(getResources().getColor(R.color.primary_color));

        bar.show();
    }

    void startFtp(ExportHelper helper) {
        String homeDir = helper.getTarget().getParentFile().getAbsolutePath();

        FtpServerActivity.start(this, homeDir);
    }

    public boolean selectPhoto() {
        SoftInputUtils.hide(getActivity());

        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        try {
            this.startActivityForResult(intent, REQUEST_PHOTO);

            return true;
        } catch (Exception e) {

        }

        return false;
    }

    void insertPictures(String[] array) {
        View focus = this.getActivity().getCurrentFocus();
        if (focus == null) {
            this.addPictures(array);
            return;
        }

        RecyclerView.ViewHolder h = mRecyclerView.findContainingViewHolder(focus);
        if (h == null) {
            this.addPictures(array);
            return;
        }

        BaseHolder holder = (BaseHolder) h;
        holder.insertPicture(array);

        // 保存一次
        this.saveNote();
    }

    void addPictures(String[] array) {

        ArrayList<BaseItem> list = new ArrayList<>(array.length * 2 + 1);

        // 创建新对象
        {
            int length = array.length;
            for (int i = 0; i < length; i++) {
                String path = array[i];

                {
                    File file = new File(path);

                    PictureItem p = PictureItem.create(mDocument, file);
                    list.add(p);
                }

                {
                    ParagraphItem p = ParagraphItem.create(mDocument, "");
                    list.add(p);
                }
            }

        }


        // 更新Document
        {
            for (BaseItem item : list) {
                mDocument.add(item);
            }
        }

        // 更新Adapter
        {
            int position = mAdapter.getItemCount();

            for (BaseItem p : list) {
                mAdapter.add(p);
            }

            int count = list.size();

            mAdapter.notifyItemChanged(position);
            mAdapter.notifyItemRangeInserted(position, count);

            mRecyclerView.scrollToPosition(position);
        }

        // 保存一次
        this.saveNote();
    }

    public void takePhoto() {
        SoftInputUtils.hide(getActivity());

        Uri imageUri = Utils.createImageUri(getActivity());

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 如果不设置EXTRA_OUTPUT getData()  获取的是bitmap数据  是压缩后的

        this.startActivityForResult(intent, REQUEST_CAMERA);

        this.mPictureUri = imageUri;
    }

    @Override
    public void onSoftInputChanged(SoftInputHelper helper, boolean oldValue, boolean newValue) {

        // 隐藏键盘时，自动保存一次
        this.saveNote();
    }

    private class ExportTask implements ObservableOnSubscribe<ExportHelper> {

        ExportHelper mHelper;

        ExportTask(ExportHelper helper) {
            this.mHelper = helper;
        }

        @Override
        public void subscribe(ObservableEmitter<ExportHelper> emitter) throws Exception {
            mHelper.export();

            emitter.onNext(mHelper);
            emitter.onComplete();
        }
    }
}
