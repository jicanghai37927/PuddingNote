package com.haiyunshan.pudding.compose.helper;

import android.text.Editable;

import com.haiyunshan.pudding.compose.adapter.ParagraphHolder;
import com.haiyunshan.pudding.compose.span.HighlightItem;
import com.haiyunshan.pudding.compose.span.Spans;
import com.haiyunshan.pudding.compose.widget.ParagraphEditText;


public class ParagraphSpanHelper {

    ParagraphHolder mHolder;
    ParagraphEditText mEdit;

    public ParagraphSpanHelper(ParagraphHolder holder, ParagraphEditText editText) {
        this.mHolder = holder;
        this.mEdit = editText;
    }

    public boolean toggleHighlight() {
        Editable text = mEdit.getText();

        HighlightItem item = Spans.HIGHLIGHT_ITEM;
        boolean result = item.match(text);
        if (result) {
            item.clear(text);
        } else {
            item.set(text);
        }

        return !result;
    }
}
