package com.haiyunshan.pudding.compose.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.haiyunshan.pudding.compose.ComposeFragment;
import com.haiyunshan.pudding.compose.document.BaseItem;
import com.haiyunshan.pudding.compose.document.Document;
import com.haiyunshan.pudding.compose.document.ParagraphItem;
import com.haiyunshan.pudding.compose.document.PictureItem;

import java.util.ArrayList;

public class DocumentAdapter extends RecyclerView.Adapter<BaseHolder> {

    private static final int PARAGRAPH      = 101;
    private static final int PICTURE        = 102;

    ArrayList<BaseItem> mList;

    ComposeFragment mParent;

    public DocumentAdapter(ComposeFragment parent) {
        this.mParent = parent;

        Document doc = parent.getDocument();
        this.mList = new ArrayList<>(doc.getBody());
    }

    public int indexOf(BaseItem item) {
        return mList.indexOf(item);
    }

    public void add(int index, BaseItem item) {
        mList.add(index, item);
    }

    public void add(BaseItem item) {
        mList.add(item);
    }

    public int remove(BaseItem item) {
        int index = this.indexOf(item);
        if (index >= 0) {
            mList.remove(index);
        }

        return index;
    }

    @Override
    public int getItemViewType(int position) {
        BaseItem item = mList.get(position);

        int type = -1;
        if (item instanceof ParagraphItem) {
            type = PARAGRAPH;
        } else if (item instanceof PictureItem) {
            type = PICTURE;
        }

        return type;
    }

    @NonNull
    @Override
    public BaseHolder onCreateViewHolder(@NonNull ViewGroup container, int viewType) {
        BaseHolder holder = null;

        if (viewType == PARAGRAPH) {
            holder = ParagraphHolder.create(mParent, container);
        } else if (viewType == PICTURE) {
            holder = PictureHolder.create(mParent, container);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseHolder holder, int position) {
        BaseItem item = mList.get(position);

        holder.onBind(position, item);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BaseHolder holder) {
        holder.onViewAttachedToWindow();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull BaseHolder holder) {
        holder.onViewDetachedFromWindow();
    }
}
