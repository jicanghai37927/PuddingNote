package com.haiyunshan.pudding.chapter;

public class BoundaryString {

    String mText;
    public int mStart;
    public int mEnd;

    int mLength;

    public BoundaryString(String text) {
        this.mText = text;
        this.mStart = 0;
        this.mEnd = text.length();

        this.mLength = text.length();
    }

    String getString() {
        return this.mText;
    }

    int length() {
        if (isEmpty()) {
            return 0;
        }

        return mEnd - mStart;
    }

    public boolean isEmpty() {
        return (mStart >= mEnd);
    }

    void set(int start, int end) {
        this.mStart = start;
        mStart = (mStart > mLength)? mLength: mStart;

        this.mEnd = end;
        mEnd = (mEnd > mLength)? mLength: mEnd;
    }

    void trim() {

        int start = mStart;
        for (int i = start; i < mEnd; i++) {
            char c = mText.charAt(i);
            if (!ChapterBook.isWhitespace(c)) {
                start = i;
                break;
            }
        }

        int end = mEnd;
        for (int i = end - 1; i >= mStart; i--) {
            char c = mText.charAt(i);
            if (!ChapterBook.isWhitespace(c)) {
                end = i + 1;
                break;
            }
        }

        this.mStart = start;
        this.mEnd = end;
    }

    int getStart() {
        return mStart;
    }

    int getEnd() {
        return mEnd;
    }

    boolean isEnd() {
        return (mEnd >= mText.length());
    }

    /**
     * 只适合短长度
     *
     * @param ch
     * @return
     */
    int indexOf(int ch) {
        return this.indexOf(ch, mStart, mEnd);
    }

    /**
     * 只适合短长度
     *
     * @param ch
     * @param fromIndex
     * @return
     */
    int indexOf(int ch, int fromIndex) {
        return this.indexOf(ch, fromIndex, mEnd);
    }

    private int indexOf(int ch, int fromIndex, int endIndex) {
        final int max = endIndex;
        if (fromIndex < 0) {
            fromIndex = 0;
        } else if (fromIndex >= max) {
            // Note: fromIndex might be near -1>>>1.
            return -1;
        }

        if (ch < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
            // handle most cases here (ch is a BMP code point or a
            // negative value (invalid code point))
            for (int i = fromIndex; i < max; i++) {
                if (mText.charAt(i) == ch) {
                    return i;
                }
            }
            return -1;
        } else {
            return indexOfSupplementary(ch, fromIndex, endIndex);
        }
    }

    private int indexOfSupplementary(int ch, int fromIndex, int endIndex) {
        if (Character.isValidCodePoint(ch)) {
            final char hi = Character.highSurrogate(ch);
            final char lo = Character.lowSurrogate(ch);
            final int max = endIndex - 1;

            for (int i = fromIndex; i < max; i++) {
                if (mText.charAt(i) == hi && mText.charAt(i + 1) == lo) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return mText.substring(mStart, mEnd);
    }
}
