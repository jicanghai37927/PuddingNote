package com.haiyunshan.pudding.compose.event;

import android.text.Layout;

public class FormatAlignmentEvent {

    public final Layout.Alignment mAlign;

    public FormatAlignmentEvent(Layout.Alignment align) {
        this.mAlign = align;
    }
}
