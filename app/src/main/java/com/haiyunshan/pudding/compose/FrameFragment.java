package com.haiyunshan.pudding.compose;


import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.background.dataset.BackgroundManager;
import com.haiyunshan.pudding.compose.event.FormatCompleteEvent;
import com.haiyunshan.pudding.compose.event.FormatFrameEvent;
import com.haiyunshan.pudding.compose.format.TextFormat;
import com.haiyunshan.pudding.frame.dataset.FrameDataset;
import com.haiyunshan.pudding.frame.dataset.FrameEntry;
import com.haiyunshan.pudding.frame.dataset.FrameManager;
import com.haiyunshan.pudding.utils.WindowUtils;
import com.haiyunshan.pudding.widget.FormatTitleBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FrameFragment extends Fragment implements FormatTitleBar.OnButtonClickListener {

    RecyclerView mRecyclerView;
    FrameAdapter mAdapter;

    FormatTitleBar mTitleBar;

    TextFormat mTextFormat;

    String mId; // 边框ID

    int mDisplayWidth;
    float mScale;
    int mFrameWidth;
    int mFrameHeight;

    public FrameFragment() {
        this.mTextFormat = null;

        this.mId = "";

        this.mScale = (1 - 0.618f);
    }

    public void setArguments(String frameId) {
        Bundle args = new Bundle();
        args.putString("frameId", frameId);

        super.setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_frame, container, false);
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
            mTitleBar.setTitle(R.string.frame_title);
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
                this.mId = args.getString("frameId", this.mId);
            }
        }

        {
            this.mDisplayWidth = WindowUtils.getDisplayWidth();
            this.mFrameWidth = getResources().getDimensionPixelSize(R.dimen.format_frame_width);
            this.mFrameHeight = getResources().getDimensionPixelSize(R.dimen.format_frame_height);

        }

        {
            FrameDataset ds = FrameManager.instance().getDataset();
            FrameEntry entry = FrameManager.instance().getDefault();

            this.mAdapter = new FrameAdapter(ds, entry);

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

    public void setFrame(String frameId) {
        this.mId = (frameId == null)? "": frameId;

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     *
     */
    private class FrameAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        ArrayList<FrameEntry> mList;

        FrameAdapter(FrameDataset ds, FrameEntry entry) {
            this.mList = new ArrayList<>();
            mList.addAll(ds.getList());
            mList.add(entry);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            int resource = R.layout.layout_frame_item;
            View view = inflater.inflate(resource, parent, false);

            FrameHolder holder = new FrameHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
            FrameEntry entry = mList.get(position);
            FrameHolder holder = (FrameHolder)h;
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
    private class FrameHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView mCardView;
        View mBgView;
        ImageView mFrameView;
        View mCheckedView;
        TextView mNameView;

        FrameEntry mEntry;
        Drawable mDrawable;

        public FrameHolder(View itemView) {
            super(itemView);

            this.mCardView = itemView.findViewById(R.id.card_color);
            this.mBgView = itemView.findViewById(R.id.view_bg);
            this.mFrameView = itemView.findViewById(R.id.iv_frame);
            this.mCheckedView = itemView.findViewById(R.id.iv_checked);
            this.mNameView = itemView.findViewById(R.id.tv_name);

            mCardView.setOnClickListener(this);
        }

        void bind(int position, FrameEntry entry) {
            this.mEntry = entry;
            this.mDrawable = null;

            mCheckedView.setVisibility(View.INVISIBLE);
            if (mId.equals(entry.getId())) {
                mCheckedView.setVisibility(View.VISIBLE);
            }

            mNameView.setText(entry.getName());

            int resid = FrameManager.instance().getResid(entry);
            if (resid != 0) {
                Drawable d = getResources().getDrawable(resid, null);
                this.mDrawable = d;

                mFrameView.setImageDrawable(d);

            } else {
                mFrameView.setImageDrawable(null);
            }

            this.applyFrame();
            this.applyFormat();
        }

        void applyFrame() {
            float scale = mEntry.getScale();
            scale = (scale <= 0 || scale > 1)? mScale: scale;

            int width = mFrameWidth;
            int height = mFrameHeight;

            int w = (int)(width / scale);
            int h = (int)(height / scale);

            {
                ViewGroup.LayoutParams params = mFrameView.getLayoutParams();
                params.width = w;
                params.height = h;

                mFrameView.setScaleX(scale);
                mFrameView.setScaleY(scale);
            }
        }

        void applyFormat() {
            if (mTextFormat == null) {
                return;
            }

            {
                String textureId = mTextFormat.getBackgroundTexture();
                int resid = BackgroundManager.instance().getResid(textureId);
                if (resid == 0) {
                    int color = mTextFormat.getBackgroundColor();
                    color = BackgroundManager.getBackground(color);
                    mBgView.setBackgroundColor(color);
                } else {
                    mBgView.setBackgroundResource(resid);
                }
            }

            {
                int color = mTextFormat.getTextColor();
                mNameView.setTextColor(color);
            }

            if (mDrawable != null) {
                int fgColor = mTextFormat.getTextColor();
                int bgColor = mTextFormat.getBackgroundColor();
                bgColor = BackgroundManager.getBackground(bgColor);
                int color = FrameManager.getColor(fgColor, bgColor);

//                ColorFilter filter = FrameManager.instance().getLighting(bgColor, color);
                ColorFilter filter = FrameManager.instance().getPorterDuff(color);
                mDrawable.setColorFilter(filter);
            }
        }

        @Override
        public void onClick(View v) {
            if (v == mCardView) {
                String id = mEntry.getId();

                if (!id.equalsIgnoreCase(mId)) {
                    mId = mEntry.getId();

                    mAdapter.notifyDataSetChanged();
                }

                EventBus.getDefault().post(new FormatFrameEvent(id));
            }
        }
    }

}
