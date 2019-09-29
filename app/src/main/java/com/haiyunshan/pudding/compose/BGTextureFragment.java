package com.haiyunshan.pudding.compose;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.background.dataset.BackgroundDataset;
import com.haiyunshan.pudding.background.dataset.BackgroundEntry;
import com.haiyunshan.pudding.background.dataset.BackgroundManager;
import com.haiyunshan.pudding.compose.event.FormatBackgroundEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class BGTextureFragment extends Fragment {

    RecyclerView mRecyclerView;
    BGAdapter mAdapter;

    String mId;
    int mFGColor;
    int mBGColor;

    public BGTextureFragment() {

        this.mId = "";
        this.mFGColor = Color.TRANSPARENT;
        this.mBGColor = Color.TRANSPARENT;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bgtexture, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.mRecyclerView = view.findViewById(R.id.recycler_list_view);
            GridLayoutManager layout = new GridLayoutManager(getActivity(), 3);
            mRecyclerView.setLayoutManager(layout);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            BackgroundDataset ds = BackgroundManager.instance().getTextures();
            mAdapter = new BGAdapter(ds);

            mRecyclerView.setAdapter(mAdapter);
        }
    }

    public void setColor(String id, int fgColor, int bgColor) {
        this.mId = (id == null)? "": id;
        this.mFGColor = fgColor;
        this.mBGColor = bgColor;

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private class BGAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        BackgroundDataset mDs;

        BGAdapter(BackgroundDataset ds) {
            this.mDs = ds;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            int resource = R.layout.layout_bg_texture_item;
            View view = inflater.inflate(resource, parent, false);

            BGHolder holder = new BGHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
            BackgroundEntry entry = mDs.get(position);
            BGHolder holder = (BGHolder)h;
            holder.bind(position, entry);
        }

        @Override
        public int getItemCount() {
            return mDs.size();
        }
    }

    private class BGHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView mCardView;
        View mTextureView;
        View mCheckedView;
        TextView mNameView;

        BackgroundEntry mEntry;

        public BGHolder(View itemView) {
            super(itemView);

            this.mCardView = itemView.findViewById(R.id.card_color);
            this.mTextureView = itemView.findViewById(R.id.iv_frame);
            this.mCheckedView = itemView.findViewById(R.id.iv_checked);
            this.mNameView = itemView.findViewById(R.id.tv_name);

            mCardView.setOnClickListener(this);
        }

        void bind(int position, BackgroundEntry entry) {
            this.mEntry = entry;

            mCheckedView.setVisibility(View.INVISIBLE);
            if (mId.equals(entry.getId())) {
                mCheckedView.setVisibility(View.VISIBLE);
            }

            mNameView.setText(entry.getName());
            mNameView.setTextColor(entry.getForeground());

            mCardView.setCardBackgroundColor(entry.getBackground());

            int resid = BackgroundManager.instance().getResid(entry.getId());
            if (resid != 0) {
                mTextureView.setBackgroundResource(resid);
            } else {
                mTextureView.setBackground(null);
            }
        }

        @Override
        public void onClick(View v) {
            String textureId = mEntry.getId();
            int fgColor = mEntry.getForeground();
            int bgColor = mEntry.getBackground();

            if ((fgColor != mFGColor) || (bgColor != mBGColor) || !(mId.equals(textureId))) {

                mId = textureId;
                mFGColor = mEntry.getForeground();
                mBGColor = mEntry.getBackground();

                mAdapter.notifyDataSetChanged();

                FormatBackgroundEvent event = new FormatBackgroundEvent(FormatBackgroundEvent.SRC_TEXTURE, textureId, fgColor, bgColor);
                EventBus.getDefault().post(event);
            }

        }
    }
}
