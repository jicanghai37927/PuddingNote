package com.haiyunshan.pudding.compose.span;

import android.text.Spannable;

import com.haiyunshan.pudding.compose.note.SpanEntity;
import com.haiyunshan.pudding.style.HighlightSpan;

public class HighlightItem extends BaseSpanItem<HighlightSpan> {

    private static final String ENTITY_TAG = "highlight"; // HighLightPoint

    HighlightItem() {
        super(HighlightSpan.class);

        this.mConcatable = false;
    }

    @Override
    protected boolean accept(HighlightSpan span) {
        return true;
    }

    @Override
    protected boolean concat(HighlightSpan span) {
        return true;
    }

    @Override
    protected HighlightSpan create() {
        return new HighlightSpan();
    }

    @Override
    protected HighlightSpan create(HighlightSpan span) {
        return new HighlightSpan(span);
    }

    @Override
    public SpanEntity toEntity(Object obj, int start, int end) {
        if (obj instanceof HighlightSpan) {
            SpanEntity en = new SpanEntity(ENTITY_TAG, start, end);

            return en;
        }

        return null;
    }

    @Override
    public Object toSpan(SpanEntity entity, Spannable text) {
        if (entity.getType().equalsIgnoreCase(ENTITY_TAG)) {
            HighlightSpan span = new HighlightSpan();

            int start = entity.getStart();
            int end = entity.getEnd();
            int flags = this.mFlags;

            text.setSpan(span, start, end, flags);
            return span;
        }

        return null;
    }

}
