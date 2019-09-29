package com.haiyunshan.pudding.chapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ChapterLayoutManager extends LinearLayoutManager {

    public ChapterLayoutManager(Context context) {
        this(context, VERTICAL, false);
    }

    public ChapterLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public boolean onRequestChildFocus(RecyclerView parent, RecyclerView.State state, View child, View focused) {
        return super.onRequestChildFocus(parent, state, child, focused);
    }
}
