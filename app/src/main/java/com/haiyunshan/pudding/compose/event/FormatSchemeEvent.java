package com.haiyunshan.pudding.compose.event;

import com.haiyunshan.pudding.scheme.dataset.SchemeEntry;

public class FormatSchemeEvent {

    public final SchemeEntry mEntry;

    public FormatSchemeEvent(SchemeEntry entry) {
        this.mEntry = entry;
    }
}
