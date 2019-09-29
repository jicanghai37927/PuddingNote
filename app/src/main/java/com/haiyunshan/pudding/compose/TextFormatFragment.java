package com.haiyunshan.pudding.compose;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.background.dataset.BackgroundManager;
import com.haiyunshan.pudding.compose.event.FormatAlignmentEvent;
import com.haiyunshan.pudding.compose.event.FormatCompleteEvent;
import com.haiyunshan.pudding.compose.event.FormatPaddingEvent;
import com.haiyunshan.pudding.compose.event.FormatSpacingEvent;
import com.haiyunshan.pudding.compose.event.FormatTextSizeEvent;
import com.haiyunshan.pudding.compose.format.TextFormat;
import com.haiyunshan.pudding.divider.SectionDividerItemDecoration;
import com.haiyunshan.pudding.font.dataset.FontEntry;
import com.haiyunshan.pudding.font.dataset.FontManager;
import com.haiyunshan.pudding.frame.dataset.FrameEntry;
import com.haiyunshan.pudding.frame.dataset.FrameManager;
import com.haiyunshan.pudding.scheme.dataset.SchemeEntry;
import com.haiyunshan.pudding.scheme.dataset.SchemeManager;
import com.haiyunshan.pudding.widget.CheckableImageView;
import com.haiyunshan.pudding.widget.FormatTitleBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class TextFormatFragment extends Fragment implements FormatTitleBar.OnButtonClickListener {

    static final int ITEM_TITLE             = 101;
    static final int ITEM_SEPARATE          = 102;

    static final int ITEM_TYPEFACE          = 202;  // 字体
    static final int ITEM_SIZE              = 204;  // 文字大小
    static final int ITEM_COLOR             = 205;  // 文本颜色
    static final int ITEM_BACKGROUND        = 206;  // 背景
    static final int ITEM_HORIZONTAL        = 207;  // 左右边距
    static final int ITEM_VERTICAL          = 208;  // 上下边距
    static final int ITEM_ALIGNMENT         = 209;  // 对齐方式
    static final int ITEM_LINE_SPACING      = 210;  // 行间距
    static final int ITEM_LETTER_SPACING    = 211;  // 字间距
    static final int ITEM_FRAME             = 212;  // 边框
    static final int ITEM_SCHEME            = 213;  // 版式

    static final int MIN_TEXT_SIZE  = 8;
    static final int MAX_TEXT_SIZE  = 108;
    static final int STEP_TEXT_SIZE = 1;

    static final int MIN_HORIZONTAL  = 8;
    static final int MAX_HORIZONTAL  = 56;
    static final int STEP_HORIZONTAL = 1;

    static final int MIN_VERTICAL  = 8;
    static final int MAX_VERTICAL  = 84;
    static final int STEP_VERTICAL = 1;

    static final int MIN_LINE_MULT  = 50;
    static final int MAX_LINE_MULT  = 300;
    static final int STEP_LINE_MULT = 10;

    static final int MIN_LETTER_MULT  = 50;
    static final int MAX_LETTER_MULT  = 200;
    static final int STEP_LETTER_MULT = 5;

    @BindView(R.id.title_bar)
    FormatTitleBar mTitleBar;

    @BindView(R.id.recycler_list_view)
    RecyclerView mRecyclerView;

    FormatAdapter mAdapter;

    BottomSheetFragment mBottomSheetFragment;

    TextFormat mTextFormat;

    Unbinder mUnbinder;

    public TextFormatFragment() {
        this.mTextFormat = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_text_format, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.mUnbinder = ButterKnife.bind(this, view);

        {
            mTitleBar.setEditable(false);
            mTitleBar.setTitle(R.string.page_setting_title);
            mTitleBar.setOnButtonClickListener(this);
        }

        {
            LinearLayoutManager layout = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(layout);

            Activity context = getActivity();
            Drawable d = context.getDrawable(R.drawable.shape_divider);
//            DividerItemDecoration decor = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
            SectionDividerItemDecoration decor = new SectionDividerItemDecoration(context);
            decor.setTop(false);
            decor.setDrawable(d);

            mRecyclerView.addItemDecoration(decor);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            this.mAdapter = new FormatAdapter(getActivity());
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        {
            Fragment p = this.getParentFragment();
            while (p != null) {

                if (p instanceof BottomSheetFragment) {
                    this.mBottomSheetFragment = (BottomSheetFragment)p;
                }

                if (p instanceof ComposeFragment) {
                    ComposeFragment f = (ComposeFragment)p;
                    TextFormat format = f.getDocument().getFormat().getParagraph();
                    this.mTextFormat = format;
                }

                p = p.getParentFragment();
            }
        }

    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();

        super.onDestroyView();


    }

    @Override
    public void onButtonClick(FormatTitleBar bar, int which) {
        switch (which) {
            case FormatTitleBar.BUTTON_CLOSE: {

                EventBus.getDefault().post(new FormatCompleteEvent());

                break;
            }
        }
    }

    void showSchemeFragment() {
        if (mBottomSheetFragment == null) {
            return;
        }

        SchemeFragment target = new SchemeFragment();
        {
            TextFormat format = this.mTextFormat;
            if (format != null) {
                target.setArguments(format.getScheme());
            }
        }
        String tag = "scheme";
        mBottomSheetFragment.push(target, tag);

    }

    void showFontFragment() {
        if (mBottomSheetFragment == null) {
            return;
        }

        FontFragment target = new FontFragment();
        {
            TextFormat format = this.mTextFormat;
            if (format != null) {
                FontEntry entry = FontManager.getInstance().obtain(format.getFont());
                String font = (entry == null) ? "" : entry.getId();
                target.setArguments(font);
            }
        }
        String tag = "font";
        mBottomSheetFragment.push(target, tag);

    }

    void showForegroundColorFragment() {
        if (mBottomSheetFragment == null) {
            return;
        }

        ColorFragment target = new ColorFragment();
        {
            TextFormat format = this.mTextFormat;
            if (format != null) {
                int color = format.getTextColor();
                target.setArguments(color);
            }
        }
        String tag = "foreground_color";
        mBottomSheetFragment.push(target, tag);
    }

    void showBackgroundFragment() {
        if (mBottomSheetFragment == null) {
            return;
        }

        BackgroundFragment target = new BackgroundFragment();
        {
            TextFormat format = this.mTextFormat;
            if (format != null) {

                String texture = format.getBackgroundTexture();
                int fgColor = format.getTextColor();
                int bgColor = format.getBackgroundColor();

                target.setArguments(texture, fgColor, bgColor);
            }
        }

        String tag = "background";
        mBottomSheetFragment.push(target, tag);
    }


    void showFrameFragment() {
        if (mBottomSheetFragment == null) {
            return;
        }

        FrameFragment target = new FrameFragment();
        {
            TextFormat format = this.mTextFormat;
            if (format != null) {
                String frameId = format.getFrame();
                target.setArguments(frameId);
            }
        }

        String tag = "frame";
        mBottomSheetFragment.push(target, tag);
    }

    /**
     *
     */
    private class FormatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        ArrayList<FormatItem> mList;

        Activity mContext;

        FormatAdapter(Activity context) {
            this.mContext = context;

            this.mList = new ArrayList<>();

            mList.add(new FormatItem(ITEM_TITLE, getString(R.string.scheme_title)));
            mList.add(new FormatItem(ITEM_SCHEME));

            mList.add(new FormatItem(ITEM_SEPARATE));

            mList.add(new FormatItem(ITEM_TYPEFACE));
            mList.add(new FormatItem(ITEM_SIZE));
            mList.add(new FormatItem(ITEM_COLOR));

            mList.add(new FormatItem(ITEM_SEPARATE));

            mList.add(new FormatItem(ITEM_ALIGNMENT));

            mList.add(new FormatItem(ITEM_SEPARATE));

            mList.add(new FormatItem(ITEM_BACKGROUND));
            mList.add(new FormatItem(ITEM_FRAME));
            mList.add(new FormatItem(ITEM_HORIZONTAL));
            mList.add(new FormatItem(ITEM_VERTICAL));

            mList.add(new FormatItem(ITEM_SEPARATE));

            mList.add(new FormatItem(ITEM_LETTER_SPACING));
            mList.add(new FormatItem(ITEM_LINE_SPACING));

        }

        @Override
        public int getItemViewType(int position) {
            int type = mList.get(position).mType;
            return type;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder = null;
            LayoutInflater inflater = mContext.getLayoutInflater();

            switch (viewType) {
                case ITEM_TITLE: {
                    int resource = R.layout.layout_paragraph_format_title_item;
                    View view = inflater.inflate(resource, parent, false);
                    holder = new TitleHolder(view);
                    break;
                }

                case ITEM_SEPARATE: {
                    int resource = R.layout.layout_paragraph_format_separate_item;
                    View view = inflater.inflate(resource, parent, false);
                    holder = new SeparateHolder(view);

                    break;
                }

                case ITEM_SCHEME: {
                    int resource = R.layout.layout_paragraph_format_scheme_item;
                    View view = inflater.inflate(resource, parent, false);
                    holder = new SchemeHolder(view);

                    break;
                }

                case ITEM_TYPEFACE: {
                    int resource = R.layout.layout_paragraph_format_font_item;
                    View view = inflater.inflate(resource, parent, false);
                    holder = new FontHolder(view);

                    break;
                }

                case ITEM_SIZE: {
                    int resource = R.layout.layout_paragraph_format_size_item;
                    View view = inflater.inflate(resource, parent, false);
                    holder = new TextSizeHolder(view, MIN_TEXT_SIZE, MAX_TEXT_SIZE, STEP_TEXT_SIZE);

                    break;
                }

                case ITEM_COLOR: {
                    int resource = R.layout.layout_paragraph_format_color_item;
                    View view = inflater.inflate(resource, parent, false);
                    holder = new TextColorHolder(view);

                    break;
                }

                case ITEM_ALIGNMENT: {
                    int resource = R.layout.layout_paragraph_format_alignment_item;
                    View view = inflater.inflate(resource, parent, false);
                    holder = new AlignmentHolder(view);

                    break;
                }

                case ITEM_BACKGROUND: {
                    int resource = R.layout.layout_paragraph_format_color_item;
                    View view = inflater.inflate(resource, parent, false);
                    holder = new BackgroundHolder(view);

                    break;
                }

                case ITEM_FRAME: {
                    int resource = R.layout.layout_paragraph_format_font_item;
                    View view = inflater.inflate(resource, parent, false);
                    holder = new FrameHolder(view);

                    break;
                }

                case ITEM_HORIZONTAL: {
                    int resource = R.layout.layout_paragraph_format_size_item;
                    View view = inflater.inflate(resource, parent, false);
                    holder = new HorizontalHolder(view, MIN_HORIZONTAL, MAX_HORIZONTAL, STEP_HORIZONTAL);

                    break;
                }

                case ITEM_VERTICAL: {
                    int resource = R.layout.layout_paragraph_format_size_item;
                    View view = inflater.inflate(resource, parent, false);
                    holder = new VerticalHolder(view, MIN_VERTICAL, MAX_VERTICAL, STEP_VERTICAL);

                    break;
                }

                case ITEM_LETTER_SPACING: {
                    int resource = R.layout.layout_paragraph_format_size_item;
                    View view = inflater.inflate(resource, parent, false);
                    holder = new LetterSpacingHolder(view, MIN_LETTER_MULT, MAX_LETTER_MULT, STEP_LETTER_MULT);

                    break;
                }

                case ITEM_LINE_SPACING: {
                    int resource = R.layout.layout_paragraph_format_size_item;
                    View view = inflater.inflate(resource, parent, false);
                    holder = new LineSpacingHolder(view, MIN_LINE_MULT, MAX_LINE_MULT, STEP_LINE_MULT);

                    break;
                }

                default: {
                    break;
                }
            }

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
            int viewType = getItemViewType(position);
            FormatItem item = mList.get(position);

            switch (viewType) {
                case ITEM_TITLE: {
                    TitleHolder holder = (TitleHolder) h;
                    holder.bind(position, item);

                    break;
                }

                case ITEM_SEPARATE: {

                    break;
                }

                case ITEM_SCHEME: {
                    SchemeHolder holder = (SchemeHolder)h;
                    holder.bind(position, item);

                    break;
                }

                case ITEM_TYPEFACE: {
                    FontHolder holder = (FontHolder)h;
                    holder.bind(position, item);

                    break;
                }

                case ITEM_SIZE: {
                    TextSizeHolder holder = (TextSizeHolder)h;
                    holder.bind(position, item);

                    break;
                }
                case ITEM_COLOR: {
                    TextColorHolder holder = (TextColorHolder)h;
                    holder.bind(position, item);

                    break;
                }

                case ITEM_ALIGNMENT: {
                    AlignmentHolder holder = (AlignmentHolder)h;
                    holder.bind(position, item);

                    break;
                }

                case ITEM_BACKGROUND: {
                    BackgroundHolder holder = (BackgroundHolder)h;
                    holder.bind(position, item);

                    break;
                }

                case ITEM_FRAME: {
                    FrameHolder holder = (FrameHolder)h;
                    holder.bind(position, item);

                    break;
                }

                case ITEM_HORIZONTAL: {
                    HorizontalHolder holder = (HorizontalHolder)h;
                    holder.bind(position, item);

                    break;
                }

                case ITEM_VERTICAL: {
                    VerticalHolder holder = (VerticalHolder)h;
                    holder.bind(position, item);

                    break;
                }

                case ITEM_LETTER_SPACING: {
                    LetterSpacingHolder holder = (LetterSpacingHolder)h;
                    holder.bind(position, item);

                    break;
                }

                case ITEM_LINE_SPACING: {
                    LineSpacingHolder holder = (LineSpacingHolder)h;
                    holder.bind(position, item);

                    break;
                }
            }

        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    private class TitleHolder extends RecyclerView.ViewHolder {

        TextView mNameView;
        public TitleHolder(View itemView) {
            super(itemView);

            this.mNameView = (TextView)itemView;
        }

        void bind(int position, FormatItem item) {
            mNameView.setText(item.mName);
        }
    }

    private class SeparateHolder extends RecyclerView.ViewHolder {

        public SeparateHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     *
     */
    private class SchemeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mNameView;
        TextView mActionView;

        SchemeHolder(View itemView) {
            super(itemView);

            this.mNameView = itemView.findViewById(R.id.tv_name);
            this.mActionView = itemView.findViewById(R.id.tv_action);

            itemView.setOnClickListener(this);
        }

        void bind(int position, FormatItem item) {
            CharSequence name = null;

            if (mTextFormat != null) {
                String scheme = mTextFormat.getScheme();
                SchemeEntry entry = SchemeManager.instance().obtain(scheme);
                name = entry.getName();
            }

            mNameView.setText(name);

        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                showSchemeFragment();
            }
        }
    }

    /**
     *
     */
    private class FontHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mValueView;

        FontHolder(View itemView) {
            super(itemView);

            this.mValueView = itemView.findViewById(R.id.tv_value);

            itemView.setOnClickListener(this);
        }

        void bind(int position, FormatItem item) {
            CharSequence name = null;

            if (mTextFormat != null) {
                String fontId = mTextFormat.getFont();
                FontManager mgr = FontManager.getInstance();
                FontEntry e = mgr.obtain(fontId);
                if (e != null) {
                    name = e.getPrettyName();
                }

                Typeface tf = mgr.getTypeface(e);
                mValueView.setTypeface(tf);
            }

            mValueView.setText(name);
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                showFontFragment();
            }
        }
    }

    /**
     *
     */
    private class TextSizeHolder extends ValueHolder implements SectionDividerItemDecoration.UpdateSectionMargin {

        TextSizeHolder(View itemView, int min, int max, int step) {
            super(itemView, min, max, step);
        }

        void bind(int position, FormatItem item) {
            this.mSize = 0;

            if (mTextFormat != null) {
                mSize = mTextFormat.getTextSize();
            }

            super.bind(position, item);
        }

        @Override
        public int getMargin() {
            return getActivity().getResources().getDimensionPixelSize(R.dimen.format_item_padding);
        }

        @Override
        void notifySizeChanged(int oldSize, int newSize) {
            EventBus.getDefault().post(new FormatTextSizeEvent(newSize));
        }
    }

    private class TextColorHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mColorView;

        TextColorHolder(View itemView) {
            super(itemView);

            this.mColorView = itemView.findViewById(R.id.view_text_color);

            itemView.setOnClickListener(this);
        }

        void bind(int position, FormatItem item) {
            int color = Color.BLACK;

            if (mTextFormat != null) {
                color = mTextFormat.getTextColor();
            }

            mColorView.setBackgroundColor(color);
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                showForegroundColorFragment();
            }
        }
    }

    /**
     *
     */
    private class AlignmentHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CheckableImageView mAlignNormalBtn;
        CheckableImageView mAlignCenterBtn;
        CheckableImageView mAlignOppositeBtn;

        Layout.Alignment mAlignment;

        AlignmentHolder(View itemView) {
            super(itemView);

            this.mAlignNormalBtn = itemView.findViewById(R.id.btn_align_normal);
            mAlignNormalBtn.setOnClickListener(this);

            this.mAlignCenterBtn = itemView.findViewById(R.id.btn_align_center);
            mAlignCenterBtn.setOnClickListener(this);

            this.mAlignOppositeBtn = itemView.findViewById(R.id.btn_align_opposite);
            mAlignOppositeBtn.setOnClickListener(this);

            this.mAlignment = Layout.Alignment.ALIGN_NORMAL;
        }

        void bind(int position, FormatItem item) {
            this.mAlignment = Layout.Alignment.ALIGN_NORMAL;

            if (mTextFormat != null) {
                mAlignment = mTextFormat.getAlignment();
            }

            this.setChecked(mAlignment);
        }

        @Override
        public void onClick(View v) {
            if (v == mAlignNormalBtn) {
                if (!mAlignNormalBtn.isChecked()) {
                    this.setChecked(mAlignNormalBtn);

                    Layout.Alignment oldValue = mAlignment;
                    mAlignment = Layout.Alignment.ALIGN_NORMAL;

                    this.notifyAlignmentChanged(oldValue, mAlignment);
                }
            } else if (v == mAlignCenterBtn) {
                if (!mAlignCenterBtn.isChecked()) {
                    this.setChecked(mAlignCenterBtn);

                    Layout.Alignment oldValue = mAlignment;
                    mAlignment = Layout.Alignment.ALIGN_CENTER;

                    this.notifyAlignmentChanged(oldValue, mAlignment);
                }
            } else if (v == mAlignOppositeBtn) {
                if (!mAlignOppositeBtn.isChecked()) {
                    this.setChecked(mAlignOppositeBtn);

                    Layout.Alignment oldValue = mAlignment;
                    mAlignment = Layout.Alignment.ALIGN_OPPOSITE;

                    this.notifyAlignmentChanged(oldValue, mAlignment);
                }
            }
        }

        void setChecked(CheckableImageView view) {
            mAlignNormalBtn.setChecked(false);
            mAlignCenterBtn.setChecked(false);
            mAlignOppositeBtn.setChecked(false);

            view.setChecked(true);
        }

        void setChecked(Layout.Alignment align) {
            mAlignNormalBtn.setChecked(false);
            mAlignCenterBtn.setChecked(false);
            mAlignOppositeBtn.setChecked(false);

            if (align == Layout.Alignment.ALIGN_NORMAL) {
                mAlignNormalBtn.setChecked(true);
            } else if (align == Layout.Alignment.ALIGN_CENTER) {
                mAlignCenterBtn.setChecked(true);
            } else if (align == Layout.Alignment.ALIGN_OPPOSITE) {
                mAlignOppositeBtn.setChecked(true);
            }
        }

        void notifyAlignmentChanged(Layout.Alignment oldValue, Layout.Alignment newValue) {
            EventBus.getDefault().post(new FormatAlignmentEvent(newValue));
        }

    }

    /**
     *
     */
    private class BackgroundHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mColorView;

        BackgroundHolder(View itemView) {
            super(itemView);

            TextView nameView = itemView.findViewById(R.id.tv_name);
            nameView.setText(R.string.format_item_background);

            this.mColorView = itemView.findViewById(R.id.view_text_color);

            itemView.setOnClickListener(this);
        }

        void bind(int position, FormatItem item) {
            int color = Color.BLACK;
            String textureId = null;

            if (mTextFormat != null) {
                color = mTextFormat.getBackgroundColor();
                color = BackgroundManager.getBackground(color);

                textureId = mTextFormat.getBackgroundTexture();
            }

            int resid = 0;
            if (!TextUtils.isEmpty(textureId)) {
                resid = BackgroundManager.instance().getResid(textureId);
            }

            if (resid != 0) {
                mColorView.setBackgroundResource(resid);
            } else {
                mColorView.setBackgroundColor(color);
            }
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                showBackgroundFragment();
            }
        }
    }

    /**
     *
     */
    private class FrameHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mValueView;

        FrameHolder(View itemView) {
            super(itemView);

            TextView nameView = itemView.findViewById(R.id.tv_name);
            nameView.setText(R.string.frame_title);

            this.mValueView = itemView.findViewById(R.id.tv_value);

            itemView.setOnClickListener(this);
        }

        void bind(int position, FormatItem item) {
            CharSequence name = null;

            if (mTextFormat != null) {
                FrameManager mgr = FrameManager.instance();
                FrameEntry entry = mgr.obtain(mTextFormat.getFrame());
                if (entry != null) {
                    name = entry.getName();
                }
            }

            mValueView.setText(name);
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                showFrameFragment();
            }
        }
    }

    /**
     *
     */
    private class HorizontalHolder extends ValueHolder implements SectionDividerItemDecoration.UpdateSectionMargin {

        HorizontalHolder(View itemView, int min, int max, int step) {
            super(itemView, min, max, step);

            mNameView.setText(R.string.horizontal_padding_name);
        }

        @Override
        void bind(int position, FormatItem item) {
            this.mSize = 0;

            if (mTextFormat != null) {
                mSize = mTextFormat.getPaddingHorizontal();
            }

            super.bind(position, item);
        }

        @Override
        public int getMargin() {
            return getActivity().getResources().getDimensionPixelSize(R.dimen.format_item_padding);
        }

        @Override
        void notifySizeChanged(int oldSize, int newSize) {
            EventBus.getDefault().post(new FormatPaddingEvent(newSize, -1));
        }
    }

    /**
     *
     */
    private class VerticalHolder extends ValueHolder implements SectionDividerItemDecoration.UpdateSectionMargin {

        VerticalHolder(View itemView, int min, int max, int step) {
            super(itemView, min, max, step);

            mNameView.setText(R.string.vertical_padding_name);
        }

        @Override
        void bind(int position, FormatItem item) {
            this.mSize = 0;

            if (mTextFormat != null) {
                mSize = mTextFormat.getPaddingVertical();
            }

            super.bind(position, item);
        }

        @Override
        public int getMargin() {
//            return getActivity().getResources().getDimensionPixelSize(R.dimen.format_item_padding);

            return 0;
        }

        @Override
        void notifySizeChanged(int oldSize, int newSize) {
            EventBus.getDefault().post(new FormatPaddingEvent(-1, newSize));
        }
    }

    /**
     *
     */
    private class LetterSpacingHolder extends ValueHolder implements SectionDividerItemDecoration.UpdateSectionMargin {

        LetterSpacingHolder(View itemView, int min, int max, int step) {
            super(itemView, min, max, step);

            mNameView.setText(R.string.letter_spacing_name);
        }

        @Override
        void bind(int position, FormatItem item) {
            this.mSize = 0;

            if (mTextFormat != null) {
                mSize = mTextFormat.getLetterSpacingMultiplier();
            }

            super.bind(position, item);
        }

        @Override
        public int getMargin() {
            return getActivity().getResources().getDimensionPixelSize(R.dimen.format_item_padding);
        }

        @Override
        CharSequence getTextValue(int size) {
            StringBuilder sb = new StringBuilder();

            int a = size / 100;
            int b = size % 100;
            int c = size % 10;

            sb.append(a);

            if (b != 0 || c != 0) {
                sb.append('.');

                if (c != 0) {
                    if (b < 10) {
                        sb.append('0');
                    }

                    sb.append(b);
                } else {
                    sb.append(b / 10);
                }
            }


            return sb;
        }

        @Override
        void notifySizeChanged(int oldSize, int newSize) {
            EventBus.getDefault().post(new FormatSpacingEvent(-1, newSize));
        }
    }

    /**
     *
     */
    private class LineSpacingHolder extends ValueHolder implements SectionDividerItemDecoration.UpdateSectionMargin {

        LineSpacingHolder(View itemView, int min, int max, int step) {
            super(itemView, min, max, step);

            mNameView.setText(R.string.line_spacing_name);
        }

        @Override
        void bind(int position, FormatItem item) {
            this.mSize = 0;

            if (mTextFormat != null) {
                mSize = mTextFormat.getLineSpacingMultiplier();
            }

            super.bind(position, item);
        }

        @Override
        public int getMargin() {
//            return getActivity().getResources().getDimensionPixelSize(R.dimen.format_item_padding);

            return 0;
        }

        @Override
        CharSequence getTextValue(int size) {
            StringBuilder sb = new StringBuilder();

            int a = size / 100;
            int b = size % 100;
            int c = size % 10;

            sb.append(a);

            if (b != 0 || c != 0) {
                sb.append('.');

                if (c != 0) {
                    sb.append(b);
                } else {
                    sb.append(b / 10);
                }
            }


            return sb;
        }

        @Override
        void notifySizeChanged(int oldSize, int newSize) {
            EventBus.getDefault().post(new FormatSpacingEvent(newSize, -1));
        }
    }

    /**
     *
     */
    private abstract class ValueHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mNameView;

        TextView mValueView;
        View mMinusView;
        View mPlusView;

        int mSize;

        int mMinValue;
        int mMaxValue;
        int mStepValue;

        ValueHolder(View itemView, int min, int max, int step) {
            super(itemView);

            this.mMinValue = min;
            this.mMaxValue = max;
            this.mStepValue = step;

            this.mNameView = itemView.findViewById(R.id.tv_name);

            this.mValueView = itemView.findViewById(R.id.tv_value);
            this.mMinusView = itemView.findViewById(R.id.iv_minus);
            mMinusView.setOnClickListener(this);
            this.mPlusView = itemView.findViewById(R.id.iv_plus);
            mPlusView.setOnClickListener(this);
        }

        void bind(int position, FormatItem item) {
            mSize = mSize < mMinValue? mMinValue: mSize;
            mSize = mSize > mMaxValue? mMaxValue: mSize;

            mValueView.setText(this.getTextValue(this.mSize));

            {
                mMinusView.setEnabled(mSize > mMinValue);
                mPlusView.setEnabled(mSize < mMaxValue);
            }
        }

        @Override
        public void onClick(View v) {
            if (v == mMinusView) {
                if (mSize > mMinValue) {
                    int oldSize = mSize;

                    int size = mSize;
                    size -= mStepValue;
                    size = (size < mMinValue)? mMinValue : size;
                    size = (size > mMaxValue)? mMaxValue : size;
                    this.mSize = size;

                    mValueView.setText(this.getTextValue(this.mSize));

                    // 通知
                    this.notifySizeChanged(oldSize, this.mSize);
                }

                {
                    mMinusView.setEnabled(mSize > mMinValue);
                    mPlusView.setEnabled(mSize < mMaxValue);
                }
            } else if (v == mPlusView) {
                if (mSize < mMaxValue) {
                    int oldSize = mSize;

                    int size = mSize;
                    size += mStepValue;
                    size = (size < mMinValue)? mMinValue: size;
                    size = (size > mMaxValue)? mMaxValue : size;

                    this.mSize = size;

                    mValueView.setText(this.getTextValue(this.mSize));

                    // 通知
                    this.notifySizeChanged(oldSize, this.mSize);
                }

                {
                    mMinusView.setEnabled(mSize > mMinValue);
                    mPlusView.setEnabled(mSize < mMaxValue);
                }
            }
        }

        CharSequence getTextValue(int size) {
            CharSequence text = null;
            if (size < 0) {
                return text;
            }

            text = getString(R.string.size_fmt, (int) size);
            return text;
        }

        abstract void notifySizeChanged(int oldSize, int newSize);
    }

    /**
     *
     */
    private class FormatItem {

        int mType;
        CharSequence mName;

        FormatItem(int type) {
            this.mType = type;
        }

        FormatItem(int type, CharSequence name) {
            this.mType = type;
            this.mName = name;
        }
    }
}
