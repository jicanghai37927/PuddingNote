package com.haiyunshan.pudding.compose;


import android.graphics.ColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.background.dataset.BackgroundManager;
import com.haiyunshan.pudding.compose.event.FormatCompleteEvent;
import com.haiyunshan.pudding.compose.event.FormatFrameEvent;
import com.haiyunshan.pudding.compose.event.FormatSchemeEvent;
import com.haiyunshan.pudding.font.dataset.FontManager;
import com.haiyunshan.pudding.frame.dataset.FrameEntry;
import com.haiyunshan.pudding.frame.dataset.FrameManager;
import com.haiyunshan.pudding.scheme.dataset.SchemeDataset;
import com.haiyunshan.pudding.scheme.dataset.SchemeEntry;
import com.haiyunshan.pudding.scheme.dataset.SchemeManager;
import com.haiyunshan.pudding.utils.WindowUtils;
import com.haiyunshan.pudding.widget.FormatTitleBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SchemeFragment extends Fragment implements FormatTitleBar.OnButtonClickListener {

    RecyclerView mRecyclerView;
    SchemeAdapter mAdapter;

    FormatTitleBar mTitleBar;

    String mId;     // 显示checked的ID
    String mPostId; // 判断事件是否发送的ID

    int mDisplayWidth;
    float mScale;
    int mFrameWidth;
    int mFrameHeight;

    public SchemeFragment() {
        this.mId = "";
        this.mPostId = "";

        this.mScale = (1 - 0.618f);
    }

    public void setArguments(String schemeId) {
        Bundle args = new Bundle();
        args.putString("schemeId", schemeId);

        super.setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scheme, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.mRecyclerView = view.findViewById(R.id.recycler_list_view);
            LinearLayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(layout);
        }

        {
            this.mTitleBar = view.findViewById(R.id.title_bar);
            mTitleBar.setTitle(R.string.scheme_title);
            mTitleBar.setEditable(false);
            mTitleBar.setBackable(true);
            mTitleBar.setOnButtonClickListener(this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            Bundle args = this.getArguments();
            if (args != null) {
                this.mId = args.getString("schemeId", "");
            }
        }

        {
            this.mDisplayWidth = WindowUtils.getDisplayWidth();
            this.mFrameWidth = getResources().getDimensionPixelSize(R.dimen.format_frame_width);
            this.mFrameHeight = getResources().getDimensionPixelSize(R.dimen.format_frame_height);

        }

        {
            SchemeDataset ds = SchemeManager.instance().getDataset();
            SchemeEntry entry = SchemeManager.instance().getDefault();

            this.mAdapter = new SchemeAdapter(ds, entry);

            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onButtonClick(FormatTitleBar bar, int which) {
        switch (which) {
            case FormatTitleBar.BUTTON_BACK: {
                getActivity().onBackPressed();

                break;
            }

            case FormatTitleBar.BUTTON_CLOSE: {

                EventBus.getDefault().post(new FormatCompleteEvent());

                break;
            }
        }
    }


    /**
     *
     */
    private class SchemeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        ArrayList<SchemeEntry> mList;

        SchemeAdapter(SchemeDataset ds, SchemeEntry entry) {
            this.mList = new ArrayList<>();
            mList.addAll(ds.getList());
            mList.add(entry);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            int resource = R.layout.layout_scheme_item;
            View view = inflater.inflate(resource, parent, false);

            SchemeHolder holder = new SchemeHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
            SchemeEntry entry = mList.get(position);
            SchemeHolder holder = (SchemeHolder)h;
            holder.bind(position, entry);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    /**
     *
     */
    private class SchemeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView mCardView;
        View mBgView;
        ImageView mFrameView;
        TextView mTextView;
        View mCheckedView;
        TextView mNameView;

        SchemeEntry mEntry;
        Drawable mDrawable;

        public SchemeHolder(View itemView) {
            super(itemView);

            this.mCardView = itemView.findViewById(R.id.card_color);
            this.mBgView = itemView.findViewById(R.id.view_bg);
            this.mFrameView = itemView.findViewById(R.id.iv_frame);
            this.mTextView = itemView.findViewById(R.id.tv_text);
            this.mCheckedView = itemView.findViewById(R.id.iv_checked);
            this.mNameView = itemView.findViewById(R.id.tv_name);

            mCardView.setOnClickListener(this);
        }

        void bind(int position, SchemeEntry entry) {
            SchemeManager.instance().applyPrefer(entry);

            {
                this.mEntry = entry;
                this.mDrawable = null;
            }

            {
                this.applyFormat();
                this.applyFrame();
            }

            {
                mCheckedView.setVisibility(View.INVISIBLE);
                if (mId.equalsIgnoreCase(entry.getId())) {
                    mCheckedView.setVisibility(View.VISIBLE);
                }

                mTextView.setText(entry.getText());
                mNameView.setText(entry.getName());
            }
        }

        void applyFrame() {

            int resid = 0;
            FrameEntry entry = null;

            {

                {
                    String id = mEntry.getFrame();
                    if (!TextUtils.isEmpty(id)) {
                        entry = FrameManager.instance().obtain(id);
                        resid = FrameManager.instance().getResid(id);
                    }
                }

                if (resid != 0) {
                    Drawable d = getResources().getDrawable(resid, null);
                    this.mDrawable = d;

                    int fgColor = mEntry.getTextColor();
                    int bgColor = mEntry.getBackgroundColor();
                    bgColor = BackgroundManager.getBackground(bgColor);
                    int color = FrameManager.getColor(fgColor, bgColor);

//                ColorFilter filter = FrameManager.instance().getLighting(bgColor, color);
                    ColorFilter filter = FrameManager.instance().getPorterDuff(color);
                    mDrawable.setColorFilter(filter);

                    mFrameView.setImageDrawable(d);

                } else {
                    mFrameView.setImageDrawable(null);
                }
            }

            if (entry != null) {
                float scale = entry.getScale();
                scale = (scale <= 0 || scale > 1) ? mScale : scale;

                int width = mFrameWidth;
                int height = mFrameHeight;

                int w = (int) (width / scale);
                int h = (int) (height / scale);

                {
                    ViewGroup.LayoutParams params = mFrameView.getLayoutParams();
                    params.width = w;
                    params.height = h;

                    mFrameView.setScaleX(scale);
                    mFrameView.setScaleY(scale);
                }
            }
        }

        void applyFormat() {

            {
                Layout.Alignment alignment = mEntry.getAlignment();

                int align = TextView.TEXT_ALIGNMENT_TEXT_START;
                align = (alignment == Layout.Alignment.ALIGN_CENTER) ? TextView.TEXT_ALIGNMENT_CENTER : align;
                align = (alignment == Layout.Alignment.ALIGN_OPPOSITE) ? TextView.TEXT_ALIGNMENT_TEXT_END : align;

                mTextView.setTextAlignment(align);
            }

            {
                int fgColor = mEntry.getTextColor();
                mTextView.setTextColor(fgColor);
            }

            {
                int bgColor = mEntry.getBackgroundColor();
                mCardView.setCardBackgroundColor(bgColor);
            }

            {
                String id = mEntry.getBackgroundTexture();
                int resid = BackgroundManager.instance().getResid(id);
                if (resid != 0) {
                    mBgView.setBackgroundResource(resid);
                } else {
                    mBgView.setBackground(null);
                }
            }

            {
                String font = mEntry.getFont();
                Typeface tf = FontManager.getInstance().getTypeface(font);
                mTextView.setTypeface(tf);
            }
        }

        @Override
        public void onClick(View v) {
            if (v == mCardView) {
                String id = mEntry.getId();

                if (!id.equalsIgnoreCase(mId)) {
                    mId = id;

                    mAdapter.notifyDataSetChanged();
                }

                if (TextUtils.isEmpty(mPostId) || !id.equalsIgnoreCase(mPostId)) {
                    mPostId = id;

                    EventBus.getDefault().post(new FormatSchemeEvent(mEntry));
                }
            }
        }

    }
}
