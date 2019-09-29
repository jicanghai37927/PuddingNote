package com.haiyunshan.pudding.compose;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.background.dataset.BackgroundDataset;
import com.haiyunshan.pudding.background.dataset.BackgroundEntry;
import com.haiyunshan.pudding.background.dataset.BackgroundManager;
import com.haiyunshan.pudding.compose.background.CustomBGColorFragment;
import com.haiyunshan.pudding.compose.event.FormatBackgroundEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class BGColorFragment extends Fragment {

    static final int ITEM_COLOR     = 1;
    static final int ITEM_CUSTOM    = 2;

    RecyclerView mRecyclerView;
    BGAdapter mAdapter;

    BottomSheetFragment mBottomSheetFragment;

    String mId;
    int mFGColor;
    int mBGColor;

    public BGColorFragment() {

        this.mId = "";
        this.mFGColor = Color.TRANSPARENT;
        this.mBGColor = Color.TRANSPARENT;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bgcolor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.mRecyclerView = view.findViewById(R.id.recycler_list_view);

            GridLayoutManager layout = new GridLayoutManager(getActivity(), 3);
            layout.setSpanSizeLookup(new BGSpanSizeLookup());

            mRecyclerView.setLayoutManager(layout);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            BackgroundDataset ds = BackgroundManager.instance().getColors();
            mAdapter = new BGAdapter(ds);

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

                }

                p = p.getParentFragment();
            }
        }

    }

    public void setColor(String id, int fgColor, int bgColor) {

        this.mId = id;
        this.mFGColor = fgColor;
        this.mBGColor = bgColor;

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    void showCustomFragment() {
        if (mBottomSheetFragment == null) {
            return;
        }

        CustomBGColorFragment target = new CustomBGColorFragment();
        {
            int color = this.mBGColor;
            target.setArguments(color);
        }

        String tag = "custom_bgcolor";
        mBottomSheetFragment.push(target, tag);
    }

    String getUri(String id) {

        BackgroundEntry target = BackgroundManager.instance().obtain(id);
        String uri = (target == null)? "": target.getUri();

        return uri;
    }

    private class BGSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

        @Override
        public int getSpanSize(int position) {
            if (position < mAdapter.mDs.size()) {
                return 1;
            }

            return 3;
        }
    }

    private class BGAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        BackgroundDataset mDs;
        boolean mCustomEnable;

        BGAdapter(BackgroundDataset ds) {
            this.mDs = ds;

            this.mCustomEnable = true;
        }

        @Override
        public int getItemViewType(int position) {
            if (position < mDs.size()) {
                return ITEM_COLOR;
            }

            return ITEM_CUSTOM;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();

            RecyclerView.ViewHolder h;

            if (viewType == ITEM_COLOR) {
                int resource = R.layout.layout_bg_color_item;
                View view = inflater.inflate(resource, parent, false);

                BGHolder holder = new BGHolder(view);
                h = holder;
            } else {
                int resource = R.layout.layout_custom_bgcolor_btn;
                View view = inflater.inflate(resource, parent, false);

                CustomHolder holder = new CustomHolder(view);
                h = holder;
            }

            return h;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
            if (position < mDs.size()) {
                BackgroundEntry entry = mDs.get(position);
                BGHolder holder = (BGHolder) h;
                holder.bind(position, entry);
            } else {

            }
        }

        @Override
        public int getItemCount() {
            int size = mDs.size();
            if (mCustomEnable) {
                size += 1;
            }

            return size;
        }
    }

    private class CustomHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mCustomBtn;

        public CustomHolder(View itemView) {
            super(itemView);

            this.mCustomBtn = itemView.findViewById(R.id.btn_custom);
            mCustomBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == mCustomBtn) {
                showCustomFragment();
            }
        }
    }

    private class BGHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView mCardView;
        View mCheckedView;
        TextView mNameView;

        BackgroundEntry mEntry;

        public BGHolder(View itemView) {
            super(itemView);

            this.mCardView = itemView.findViewById(R.id.card_color);
            this.mCheckedView = itemView.findViewById(R.id.iv_checked);
            this.mNameView = itemView.findViewById(R.id.tv_name);

            mCardView.setOnClickListener(this);
        }

        void bind(int position, BackgroundEntry entry) {

            this.mEntry = entry;

            String textureUri = getUri(mId);

            mCheckedView.setVisibility(View.INVISIBLE);
            if ((entry.getBackground() == mBGColor) && TextUtils.isEmpty(textureUri)) {
                mCheckedView.setVisibility(View.VISIBLE);
            }

            mNameView.setText(entry.getName());
            mNameView.setTextColor(entry.getForeground());

            mCardView.setCardBackgroundColor(entry.getBackground());
        }

        @Override
        public void onClick(View v) {
            String textureId = mEntry.getId();

            int fgColor = mEntry.getForeground();
            int bgColor = mEntry.getBackground();

            if ((fgColor != mFGColor) || (bgColor != mBGColor)) {

                mId = textureId;
                mFGColor = mEntry.getForeground();
                mBGColor = mEntry.getBackground();

                mAdapter.notifyDataSetChanged();

                FormatBackgroundEvent event = new FormatBackgroundEvent(FormatBackgroundEvent.SRC_COLOR, fgColor, bgColor);
                EventBus.getDefault().post(event);
            }

        }
    }
}
