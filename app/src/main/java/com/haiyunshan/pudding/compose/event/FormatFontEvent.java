package com.haiyunshan.pudding.compose.event;

import com.haiyunshan.pudding.font.dataset.FontEntry;

public class FormatFontEvent {

    public final FontEntry mFont;

    public FormatFontEvent(FontEntry entry) {
        this.mFont = entry;
    }
}
