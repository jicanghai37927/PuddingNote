package com.haiyunshan.pudding.compose.span;

import android.text.Spannable;
import android.text.Spanned;

import com.haiyunshan.pudding.compose.note.SpanEntity;

import java.util.List;

public class StyleUtils {

    static final BaseSpanItem[] SPANS = new BaseSpanItem[] {

            // 字符

            Spans.HIGHLIGHT_ITEM,


            // 段落

    };

    public static final void getSpans(List<SpanEntity> list, Spanned text) {
        BaseSpanItem[] array = SPANS;

        Object[] spans = text.getSpans(0, text.length(), Object.class);
        for (Object obj : spans) {
            int start = text.getSpanStart(obj);
            int end = text.getSpanEnd(obj);

            for (BaseSpanItem item : array) {
                SpanEntity en = item.toEntity(obj, start, end);
                if (en != null) {

                    list.add(en);

                    break;
                }
            }
        }
    }

    public static final void toSpans(List<SpanEntity> list, Spannable text) {
        BaseSpanItem[] array = SPANS;

        for (SpanEntity entity : list) {
            for (BaseSpanItem item : array) {
                Object obj = item.toSpan(entity, text);
                if (obj != null) {
                    break;
                }
            }
        }
    }
}
