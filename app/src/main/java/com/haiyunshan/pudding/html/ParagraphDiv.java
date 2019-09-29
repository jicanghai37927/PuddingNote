package com.haiyunshan.pudding.html;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.haiyunshan.pudding.chapter.BoundaryString;
import com.haiyunshan.pudding.chapter.ChapterBook;

import java.util.ArrayList;

public class ParagraphDiv extends BaseDiv {

    CharSequence mText;

    ArrayList<TextLine> mList;

    public ParagraphDiv(HtmlPage page, CharSequence text) {
        super(page);

        this.mText = text;
    }

    public CharSequence getText() {
        return mText;
    }

    public CharSequence getPrettyContent(boolean beginSeparator, boolean endSeparator) {
        if (mList == null) {
            this.mList = getLines(mText.toString());
        }

        ArrayList<TextLine> list = new ArrayList<>(mList);

        this.trimAll();

        CharSequence text;
        text = this.getFormattedContent(beginSeparator, endSeparator);

        mList = list;

        return text;
    }

    CharSequence getFormattedContent(boolean beginSeparator, boolean endSeparator) {
        if (mList.isEmpty()) {
            return "";
        }

        boolean firstDiv = (mPage.get(0) == this);

        String prefix = "\u3000\u3000"; // 2个空白字符
        String separator = "\n\n";

        String strText = mList.get(0).mText;
        SpannableStringBuilder ssb = new SpannableStringBuilder(strText);
        ssb.clear();

        boolean isEmpty;

//        char c = strText.charAt(mList.get(0).mBegin);
//        Log.w("AA", c + " = " + (int)c);

        BoundaryString text = new BoundaryString(strText);
        int size = mList.size();
        for (int i = 0; i < size; i++) {
            TextLine line = mList.get(i);
            line.update(text);

            // 空行，不要了
            isEmpty = text.isEmpty();
            if (isEmpty) {
                continue;
            }

            // 内容
            String str = text.toString();

            // 分割符
            {
                if (ssb.length() != 0) {
                    ssb.append(separator);
                }

                if (beginSeparator && ssb.length() == 0) {
                    ssb.append('\n');
                }


                // 前置留白
                if (i == 0 && firstDiv) {
                    // 第一个div，第一行，不添加空白
                } else {
                    ssb.append(prefix);
                }
            }

            // 内容
            {
                if (i == 0 && firstDiv) {
                    CharSequence ss = this.createTitle(str, true);
                    ssb.append(ss);
                } else {
                    ssb.append(str);
                }
            }

        }

        if (endSeparator) {
            ssb.append('\n');
        }

        return ssb;

    }

    CharSequence createTitle(String text, boolean bigger) {
        SpannableString ss = new SpannableString(text);

        int start = 0;
        int end = text.length();
        int flags = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

        ss.setSpan(new StyleSpan(Typeface.BOLD), start, end, flags);
        if (bigger) {
            ss.setSpan(new RelativeSizeSpan(1.2f), start, end, flags);
        }
//        ss.setSpan(new UnderlineSpan(), start, end, flags);

        return ss;
    }

    void trimAll() {

        for (int i = mList.size() - 1; i >= 0; i--) {
            TextLine line = mList.get(i);
            if (line.isEmpty()) {
                mList.remove(i);
            }
        }

    }

    ArrayList<TextLine> getLines(String text) {

        char separator = '\n';

        int length = text.length();

        int capacity = length;
        capacity /= 200;
        capacity = (capacity < 10)? 10: capacity;

        ArrayList<TextLine> list = new ArrayList<>(capacity);

        // 执行分行
        {
            int sl = 1; // 分割符长度

            int begin = 0;
            while (true) {
                int pos = text.indexOf(separator, begin);

                if (pos >= 0) {
                    TextLine line = new TextLine(text, begin, pos + sl);
                    list.add(line);

                    begin = line.mEnd;

                    if (begin == text.length()) {
                        break;
                    }

                } else {
                    if (begin != text.length()) {
                        TextLine line = new TextLine(text, begin, text.length());
                        list.add(line);
                    }

                    break;
                }
            }
        }

        return list;
    }
}

class TextLine {

    String mText;

    int mBegin;
    int mEnd;

    TextLine(String text, int begin, int end) {

        this.mText = text;
        this.mBegin = begin;
        this.mEnd = end;
    }

    int getStart(boolean trim) {
        if (!trim) {
            return this.mBegin;
        }

        String text = this.mText;

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

        String text = this.mText;

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

    String getText() {
        int start = this.getStart(true);
        int end = this.getEnd(true);

        if (start >= end) {
            return "";
        }

        return mText.substring(start, end);
    }

    @Override
    public String toString() {
        return "(" + mBegin + ", " + mEnd + ")";
    }
}

