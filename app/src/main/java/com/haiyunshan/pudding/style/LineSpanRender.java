package com.haiyunshan.pudding.style;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.Spanned;
import android.text.TextPaint;
import android.widget.TextView;

import com.haiyunshan.pudding.array.ArrayUtils;
import com.haiyunshan.pudding.array.GrowingArrayUtils;

public class LineSpanRender<T extends LineSpan> {

    SpanSet<T> mLineSpans;

    TextView mView;

    Rect sTempRect = new Rect();
    private final T[] NO_PARA_SPANS;

    /**
     *
     * @param view
     * @param type
     */
    public LineSpanRender(TextView view, Class<T> type) {

        this.mView = view;
        this.NO_PARA_SPANS = ArrayUtils.emptyArray(type);

        mLineSpans = new SpanSet<>(type);
    }

    /**
     *
     * @param canvas
     */
    public void draw(Canvas canvas) {

        if (mView == null || mView.getLayout() == null) {
            return;
        }

        canvas.save();
        canvas.translate(mView.getPaddingLeft(), mView.getPaddingTop());

        this.drawBackground(canvas);

        canvas.restore();
    }

    /**
     *
     * @param canvas
     */
    public void drawBackground(Canvas canvas) {

        final long lineRange = getLineRangeForDraw(canvas);
        int firstLine = unpackRangeStartFromLong(lineRange);
        int lastLine = unpackRangeEndFromLong(lineRange);

        if (lastLine < 0) {
            return;
        }

        Spanned buffer = (Spanned) (mView.getText());
        int textLength = buffer.length();
        mLineSpans.init(buffer, 0, textLength);

        if (mLineSpans.numberOfSpans <= 0) {
            mLineSpans.recycle();
            return;
        }

        TextPaint paint = mView.getPaint();

        T[] spans = NO_PARA_SPANS;
        int spansLength = 0;

        int previousLineEnd = mView.getLayout().getLineStart(firstLine);
        int previousLineBottom = mView.getLayout().getLineTop(firstLine);

        int spanEnd = 0;
        for (int i = firstLine; i <= lastLine; i++) {

            int start = previousLineEnd;
            int end = mView.getLayout().getLineStart(i + 1);
            previousLineEnd = end;

            int ltop = previousLineBottom;
            int lbottom = mView.getLayout().getLineTop(i + 1);
            previousLineBottom = lbottom;

            int lbaseline = lbottom - mView.getLayout().getLineDescent(i);

//            if (start >= spanEnd)
            {

                // These should be infrequent, so we'll use this so that
                // we don't have to check as often.
//                spanEnd = mLineSpans.getNextTransition(start, textLength);

                // All LineBackgroundSpans on a line contribute to its background.
                spansLength = 0;

                // Duplication of the logic of getParagraphSpans
                if (start != end || start == 0) {

                    // Equivalent to a getSpans(start, end), but filling the 'spans' local
                    // array instead to reduce memory allocation
                    for (int j = 0; j < mLineSpans.numberOfSpans; j++) {
                        // equal test is valid since both intervals are not empty by
                        // construction
                        if (mLineSpans.spanStarts[j] >= end ||
                                mLineSpans.spanEnds[j] <= start) {
                            continue;
                        }

                        spans = GrowingArrayUtils.append(spans, spansLength, mLineSpans.spans[j]);
                        spansLength++;
                    }
                }
            }

            for (int n = 0; n < spansLength; n++) {
                T span = spans[n];

                span.draw(canvas, mView.getLayout(), paint,
                        ltop, lbaseline, lbottom,
                        buffer, start, end,
                        i);
            }
        }


        mLineSpans.recycle();
    }

    public long getLineRangeForDraw(Canvas canvas) {
        int dtop, dbottom;

        synchronized (sTempRect) {
            if (!canvas.getClipBounds(sTempRect)) {
                // Negative range end used as a special flag
                return packRangeInLong(0, -1);
            }

            dtop = sTempRect.top;
            dbottom = sTempRect.bottom;
        }

        final int top = Math.max(dtop, 0);
        final int bottom = Math.min(mView.getLayout().getLineTop(mView.getLineCount()), dbottom);

        if (top >= bottom) return packRangeInLong(0, -1);
        return packRangeInLong(mView.getLayout().getLineForVertical(top), mView.getLayout().getLineForVertical(bottom));
    }

    public long packRangeInLong(int start, int end) {
        return (((long) start) << 32) | end;
    }

    /**
     * Get the start value from a range packed in a long by {@link #packRangeInLong(int, int)}
     * @see #unpackRangeEndFromLong(long)
     * @see #packRangeInLong(int, int)
     * @hide
     */
    public int unpackRangeStartFromLong(long range) {
        return (int) (range >>> 32);
    }

    /**
     * Get the end value from a range packed in a long by {@link #packRangeInLong(int, int)}
     * @see #unpackRangeStartFromLong(long)
     * @see #packRangeInLong(int, int)
     * @hide
     */
    public int unpackRangeEndFromLong(long range) {
        return (int) (range & 0x00000000FFFFFFFFL);
    }

}
