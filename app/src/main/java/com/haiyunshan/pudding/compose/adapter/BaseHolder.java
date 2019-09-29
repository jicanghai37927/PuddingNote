package com.haiyunshan.pudding.compose.adapter;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.haiyunshan.pudding.compose.ComposeFragment;
import com.haiyunshan.pudding.compose.document.BaseItem;
import com.haiyunshan.pudding.compose.document.Document;
import com.haiyunshan.pudding.compose.state.BaseState;

public class BaseHolder<T extends BaseItem> extends RecyclerView.ViewHolder {

    int mPosition;
    T mItem;

    Document mDocument;
    DocumentAdapter mAdapter;
    RecyclerView mRecyclerView;
    ComposeFragment mParentFragment;

    Handler mHandler;
    Activity mContext;

    public BaseHolder(ComposeFragment parent, View itemView) {
        super(itemView);

        this.mParentFragment = parent;
        this.mDocument = parent.getDocument();
        this.mAdapter = parent.getAdapter();
        this.mRecyclerView = parent.getRecyclerView();

        this.mHandler = new Handler();

        this.mContext = parent.getActivity();
    }

    public ComposeFragment getParent() {
        return mParentFragment;
    }

    public T getItem() {
        return mItem;
    }

    public BaseState getState() {
        BaseState state = mParentFragment.getStateMachine().peek();
        return state;
    }

    @CallSuper
    public void onBind(int position, T item) {
        this.mPosition = position;
        this.mItem = item;
    }

    @CallSuper
    public void onViewAttachedToWindow() {

    }

    @CallSuper
    public void onViewDetachedFromWindow() {
        this.onSave();
    }

    @CallSuper
    public void onSave() {

    }

    public boolean insertPicture(String[] array) {
        return false;
    }

    protected void setModified(long modified) {
        mItem.setModified(modified);

        long time = mDocument.getModified();
        if (modified > time) {
            mDocument.setModified(modified);
        }
    }
}
