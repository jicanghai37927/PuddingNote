package com.haiyunshan.pudding.compose.document;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;

import com.haiyunshan.pudding.compose.note.ParagraphEntity;
import com.haiyunshan.pudding.compose.span.StyleUtils;
import com.haiyunshan.pudding.utils.UUIDUtils;

public class ParagraphItem extends BaseItem<ParagraphEntity> {

    CharSequence mText;

    int mSelectionStart = -1;
    int mSelectionEnd = -1;

    public static final ParagraphItem create(Document document, CharSequence text) {
        ParagraphEntity entity = new ParagraphEntity(UUIDUtils.next());

        ParagraphItem item = new ParagraphItem(document, entity, text);
        return item;
    }

    ParagraphItem(Document document, ParagraphEntity entity) {
        this(document, entity, entity.getText());
    }

    ParagraphItem(Document document, ParagraphEntity entity, CharSequence text) {
        super(document, entity);

        SpannableStringBuilder ss = new SpannableStringBuilder(text);
        this.mText = ss;

        // Spans
        StyleUtils.toSpans(entity.getSpans(), ss);

    }

    @Override
    public ParagraphEntity getEntity() {
        {
            mEntity.setText(mText.toString());
            mEntity.getSpans().clear();
        }

        // Spans
        if (mText instanceof Spanned) {
            StyleUtils.getSpans(mEntity.getSpans(), (Spanned)mText);
        }

        mEntity.setText(mText.toString());

        return super.getEntity();
    }

    @Override
    public boolean isEmpty() {
        return TextUtils.isEmpty(mText);
    }

    public CharSequence getText() {
        return mText;
    }

    public void setText(CharSequence text) {
        this.mText = text;
    }

    public boolean hasSelection() {
        return (mSelectionStart >= 0) && (mSelectionEnd >= 0);
    }

    public void setSelection(int start, int end) {
        this.mSelectionStart = start;
        this.mSelectionEnd = end;
    }

    public int getSelectionStart() {
        return mSelectionStart;
    }

    public int getSelectionEnd() {
        return mSelectionEnd;
    }
}