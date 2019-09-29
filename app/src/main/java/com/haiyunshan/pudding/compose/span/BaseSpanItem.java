package com.haiyunshan.pudding.compose.span;

import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;

import com.haiyunshan.pudding.compose.note.SpanEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public abstract class BaseSpanItem<T> {

    Class<T> mType;

    int mFlags;
    boolean mConcatable;

    int[] mSelection;
    ArrayList<SpanObject> mList;

    BaseSpanItem(Class<T> type) {
        this.mType = type;
        this.mConcatable = false;
        this.mFlags = Spanned.SPAN_EXCLUSIVE_INCLUSIVE;

        this.mSelection = new int[2];
        this.mList = new ArrayList<>();
    }

    /**
     * 匹配
     *
     * @param text
     * @return
     */
    public boolean match(Editable text) {
        int[] selection = getSelection(text);
        if (isEmpty(selection)) {
            return false;
        }

        ArrayList<SpanObject> spanSet = this.mList;
        int start = selection[0];
        int end = selection[1];

        T[] spans = text.getSpans(start, end, mType);
        for (T s : spans) {
            if (this.accept(s)) {
                start = text.getSpanStart(s);
                end = text.getSpanEnd(s);
                spanSet.add(new SpanObject(s, start, end));
            }
        }

        if (spanSet.isEmpty()) {
            return false;
        }

        Collections.sort(spanSet, new Comparator<SpanObject>() {
            @Override
            public int compare(SpanObject o1, SpanObject o2) {
                return o1.mStart - o2.mStart;
            }
        });

        boolean result;

        start = selection[0];
        end = selection[1];

        while (true) {
            SpanObject obj = spanSet.remove(0);
            if (obj.mStart > start) {
                result = false;
                break;
            }
            start = (obj.mEnd > start)? obj.mEnd: start;

            if (spanSet.isEmpty()) {
                result = (start >= end);
                break;
            }
        }

        return result;
    }

    /**
     * 设置
     *
     * @param text
     */
    public void set(Editable text) {
        int[] selection = getSelection(text);
        if (isEmpty(selection)) {
            return;
        }

        int start = selection[0];
        int end = selection[1];
        int flags = this.mFlags;

        if (mConcatable) { // 连接前后Span

            int s1 = start - 1;
            s1 = Math.max(0, s1);
            int e1 = end + 1;
            e1 = Math.min(text.length(), e1);

            T[] spans = text.getSpans(s1, e1, mType);
            for (T s : spans) {
                if (this.concat(s)) {
                    int a = text.getSpanStart(s);
                    int b = text.getSpanEnd(s);
                    int f = text.getSpanFlags(s);

                    start = (a < start) ? a : start;
                    end = (b > end) ? b : end;
                    flags = f;

                    text.removeSpan(s);
                }
            }

        } else { // 清除范围内的Span

            T[] spans = text.getSpans(start, end, mType);
            for (T s : spans) {
                int a = text.getSpanStart(s);
                int b = text.getSpanEnd(s);
                if (a >= start && b <= end) {
                    text.removeSpan(s);
                }

            }

        }

        text.setSpan(this.create(), start, end, flags);
    }

    /**
     * 清除
     *
     * @param text
     */
    public void clear(Editable text) {
        int[] selection = getSelection(text);
        if (isEmpty(selection)) {
            return;
        }

        int start = selection[0];
        int end = selection[1];

        T[] spans = text.getSpans(start, end, mType);
        for (T s : spans) {

            if (!this.accept(s)) {
                continue;
            }

            start = text.getSpanStart(s);
            end = text.getSpanEnd(s);
            int flags = text.getSpanFlags(s);

            if (start < selection[0]) {
                text.removeSpan(s);

                if (end <= selection[1]) {
                    text.setSpan(this.create(s), start, selection[0], flags);

                } else {
                    text.setSpan(this.create(s), start, selection[0], flags);
                    text.setSpan(this.create(s), selection[1], end, flags);
                }

            } else if (start >= selection[0]){
                text.removeSpan(s);

                if (end <= selection[1]) { // 完全在Selection内的Span

                } else {
                    text.setSpan(this.create(s), selection[1], end, flags);
                }
            }

        }
    }

    /**
     *
     * @param span
     * @return
     */
    protected abstract boolean accept(T span);

    /**
     *
     * @param span
     * @return
     */
    protected abstract boolean concat(T span);

    /**
     *
     * @return
     */
    protected abstract T create();

    /**
     *
     * @param span
     * @return
     */
    protected abstract T create(T span);

    /**
     * Span转化为Entity
     *
     * @param obj
     * @param start
     * @param end
     * @return
     */
    public abstract SpanEntity toEntity(Object obj, int start, int end);

    /**
     * Entity转化为Span
     *
     * @param entity
     * @param text
     * @return
     */
    public abstract Object toSpan(SpanEntity entity, Spannable text);

    protected int[] getSelection(Editable text) {
        int start = Selection.getSelectionStart(text);
        int end = Selection.getSelectionEnd(text);
        if (start < 0 || end < 0) {
            return null;
        }

        int a = Math.min(start, end);
        int b = Math.max(start, end);
        start = a;
        end = b;

        mSelection[0] = start;
        mSelection[1] = end;
        return mSelection;
    }

    final boolean isEmpty(int[] selection) {
        return (selection == null || selection[0] == selection[1]);
    }

    private class SpanObject {

        Object mSpan;
        int mStart;
        int mEnd;

        public SpanObject(Object span, int start, int end) {
            this.mSpan = span;
            this.mStart = start;
            this.mEnd = end;
        }

    }
}
