package com.haiyunshan.pudding.compose.state;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.haiyunshan.pudding.compose.BottomSheetFragment;
import com.haiyunshan.pudding.compose.ComposeFragment;
import com.haiyunshan.pudding.compose.adapter.DocumentAdapter;
import com.haiyunshan.pudding.compose.document.Document;

public class BaseState {

    String mId;

    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    DocumentAdapter mAdapter;
    BottomSheetFragment mBottomSheet;

    Document mDocument;

    Handler mHandler;

    ComposeFragment mParent;
    Activity mContext;

    BaseState(String id, ComposeFragment parent) {
        this.mId = id;
        this.mParent = parent;
        this.mContext = parent.getActivity();

        this.mToolbar = parent.getToolbar();
        this.mRecyclerView = parent.getRecyclerView();
        this.mAdapter = parent.getAdapter();
        this.mBottomSheet = parent.getBottomSheet();

        this.mDocument = parent.getDocument();

        this.mHandler = parent.getHandler();
    }

    @CallSuper
    public void onEnter() {

    }

    @CallSuper
    public void onExit() {

    }

    public boolean onBackPressed() {
        return false;
    }

}
