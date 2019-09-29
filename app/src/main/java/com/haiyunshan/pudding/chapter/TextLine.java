package com.haiyunshan.pudding.chapter;

import android.text.TextUtils;

public class TextLine {

    String mTitle;

    ChapterBook mBook;
    int mBegin;
    int mEnd;
    int mIndex;

    int mContentLength;

    int mTitleStart;
    int mTitleEnd;

    TextLine(ChapterBook book, int begin, int end, int index) {

        this.mBook = book;
        this.mBegin = begin;
        this.mEnd = end;
        this.mContentLength = (end - begin - 1);

        this.mIndex = index;

        this.mTitleStart = -1;
        this.mTitleEnd = -1;
    }

    int contentLength() {
        return mContentLength;
    }


    int wordLength() {
        int start = this.getStart(true);
        int end = this.getEnd(true);

        if (start >= end) {
            return 0;
        }

        return (end - start);
    }

    String getTitle() {

        TextLine line = this;
        if (!line.isTitle()) {
            return "";
        }

        if (!TextUtils.isEmpty(mTitle)) {
            return mTitle;
        }

        String text = mBook.mText;

        int start = line.mTitleStart;
        int end = line.mTitleEnd;
        mTitle = text.substring(start, end);

        return mTitle;
    }

    int getStart(boolean trim) {
        if (!trim) {
            return this.mBegin;
        }

        String text = mBook.mText;

        TextLine line = this;
        int start = line.mBegin;
        int end = line.mEnd;
        int last = end - 1;

        for (int i = start; i < last; i++) {
            char c = text.charAt(i);
            if (!ChapterBook.isWhitespace(c)) {
                return i;
            }
        }

        return end;
    }

    int getEnd(boolean trim) {
        if (!trim) {
            return this.mEnd;
        }

        String text = mBook.mText;

        TextLine line = this;
        int start = line.mBegin;
        int end = line.mEnd;
        int last = end - 1;

        for (int i = last; i > start; i--) {
            char c = text.charAt(i);
            if (!ChapterBook.isWhitespace(c)) {
                return (i + 1);
            }
        }

        return start;
    }

    void update(BoundaryString text) {
        int start = getStart(true);
        int end = getEnd(true);

        text.mStart = start;
        text.mEnd = end;
    }

    boolean isEmpty() {
        return (this.mBegin == getEnd(true));
    }

    boolean isTitle() {
        return (mTitleStart >= 0 && mTitleEnd >= 0);
    }

    String getText() {
        int start = this.getStart(true);
        int end = this.getEnd(true);

        if (start >= end) {
            return "";
        }

        return mBook.mText.substring(start, end);
    }

    @Override
    public String toString() {
        return "(" + mBegin + ", " + mEnd + ")";
    }
}
