package com.haiyunshan.pudding.chapter;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import java.util.ArrayList;

public class Chapter {

    static final char[] APPENDABLE 	= new char[] { '\uff0c', '\u201c', '\u2018', '\u300c', '\u3001' }; // ，“‘「、
    static final char[] FORCE 		= new char[] { '\uff0c', '\u201d', '\u2019', '\u300d', '\u3001' }; // ，”’」、
    static final char[] CONCATABLE 	= new char[] { '\uff0c', '\u3002', '\u201c', '\uff1b', '\u3001', '\u2014', '\uff01', '\uff1f' }; // ，。“；、—！？

    String mName;

    String mTitle;
    String mSubtitle;

    ChapterBook mBook;
    String mText;
    ArrayList<TextLine> mList;

    Chapter(ChapterBook book, int initialCapacity) {
        this.mName = null;

        this.mBook = book;
        this.mText = book.mText;
        this.mList = new ArrayList<>(initialCapacity);
    }

    public String getTitle() {
        if (mTitle != null) {
            return mTitle;
        }

        this.buildTitle();
        return mTitle;
    }

    public String getSubtitle() {
        if (mSubtitle != null) {
            return mSubtitle;
        }

        this.buildTitle();
        return mSubtitle;
    }

    public String getName(boolean trim) {

        if (mList.isEmpty()) {
            return "";
        }

        if (!TextUtils.isEmpty(mName)) {
            return mName;
        }

        TextLine line = this.getTitleLine(true);
        int start = line.mTitleStart;
        if (start < 0) {
            start = line.mBegin;
        }

        if (!trim) {
            start = line.mBegin;
        }

        int length = 0;
        if (line.mTitleEnd > 0) {
            length = line.mTitleEnd - line.mTitleStart;
        }

        mName = mText.substring(start, line.mEnd);
        mName = mName.trim();

        if (trim) {
            if (length > 0 && length < mName.length()) {
                StringBuffer sb = new StringBuffer(mName.length() + 1);
                sb.append(mName.substring(0, length));
                sb.append('\n');
                sb.append(mName.substring(length).trim());

                mName = sb.toString();
            }
        }

        return mName;
    }

    public String getContent() {
        int start = this.getStart();
        int end = this.getEnd();
        if (start == 0 && end == mText.length()) {
            return mText;
        }

        return mText.substring(start, end);
    }

    public CharSequence getPrettyContent(boolean concat, boolean beginSeparator, boolean endSeparator) {
        ArrayList<TextLine> list = new ArrayList<>(mList);

        this.trimAll();
        CharSequence text;
        if (mBook.size() == 1 && this.indexOfTitle() < 0) {
            text = this.getSingleContent(concat, beginSeparator, endSeparator);
        } else {
            text = this.getFormattedContent(concat, beginSeparator, endSeparator);
        }

        mList = list;

        return text;
    }

    TextLine getTitleLine(boolean useFirst) {
        if (mList.isEmpty()) {
            return null;
        }

        for (TextLine line : mList) {
            if (line.isTitle()) {
                return line;
            }
        }

        if (useFirst) {
            return mList.get(0);
        }

        return null;
    }

    void buildTitle() {

        if (mList.isEmpty()) {
            this.mTitle = "";
            this.mSubtitle = "";

            return;
        }

        if (mTitle != null) {
            return;
        }

        TextLine line = getTitleLine(false);
        if (line == null) {
            this.mTitle = "";
            this.mSubtitle = "";

            return;
        }

        int start = line.mTitleStart;
        int end = line.mTitleEnd;

        String s1 = line.mBook.mText.substring(start, end);
        String s2 = line.mBook.mText.substring(end + 1, line.mEnd - 1);
        s2 = ChapterBook.trim(s2);

        this.mTitle = s2;
        this.mSubtitle = s1;
    }

    void trim() {

        while (!mList.isEmpty()) {
            TextLine line = mList.get(0);
            if (line.isEmpty()) {
                mList.remove(0);
                continue;
            }

            break;
        }

    }

    void trimAll() {

        for (int i = mList.size() - 1; i >= 0; i--) {
            TextLine line = mList.get(i);
            if (line.isEmpty()) {
                mList.remove(i);
            }
        }

    }

    void add(Chapter chapter) {
        mList.addAll(chapter.mList);
    }

    public boolean hasTitle() {
        if (mList.isEmpty()) {
            return false;
        }

        for (TextLine line : mList) {
            if (line.isTitle()) {
                return true;
            }
        }

        return false;
    }

    boolean beginWithTitle() {
        if (mList.isEmpty()) {
            return false;
        }

        TextLine line = mList.get(0);
        return (line.isTitle());
    }

    int indexOfTitle() {
        if (mList.isEmpty()) {
            return -1;
        }

        int size = mList.size();
        for (int i = 0; i < size; i++) {
            TextLine line = mList.get(i);
            if (line.isTitle()) {
                return i;
            }
        }

        return -1;
    }

    int length() {
        return (getEnd() - getStart());
    }

    int getStart() {
        if (mList.isEmpty()) {
            return 0;
        }

        return mList.get(0).mBegin;
    }

    int getEnd() {
        if (mList.isEmpty()) {
            return mText.length();
        }

        return mList.get(mList.size() - 1).mEnd;
    }

    CharSequence getSingleContent(boolean concat, boolean beginSeparator, boolean endSeparator) {
        if (mList.isEmpty()) {
            return "";
        }

        int min = -2;
        int max = 2;

        String prefix = "\u3000\u3000"; // 2个空白字符
        String separator = "\n\n";

        SpannableStringBuilder ssb = new SpannableStringBuilder(mText, getStart(), getEnd());
        ssb.clear();

        boolean isEmpty;

        BoundaryString text = new BoundaryString(mText);
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
            boolean canAppend = false;
            boolean shouldConcat = false;
            if (concat) {
                boolean force = this.isForceConcat(str);

                canAppend = force ? force : this.isAppendable(ssb);
                shouldConcat = force ? force : this.isConcatable(str);
                if (i >= min && i <= max) {
                    shouldConcat = false;
                }
            }

            if (canAppend && shouldConcat) {

            } else {

                if (ssb.length() != 0) {
                    ssb.append(separator);
                }

                if (beginSeparator && ssb.length() == 0) {
                    ssb.append('\n');
                }

                // 前置留白
                if (i > 0) {
                    ssb.append(prefix);
                }

            }

            // 内容
            if (i == 0) {
                CharSequence ss = this.createTitle(str, true);
                ssb.append(ss);
            } else {
                ssb.append(str);
            }

        }

        if (endSeparator) {
            ssb.append('\n');
        }

        return ssb;
    }

    CharSequence getFormattedContent(boolean concat, boolean beginSeparator, boolean endSeparator) {
        if (mList.isEmpty()) {
            return "";
        }

        int indexOfTitle = this.indexOfTitle();
        int min = indexOfTitle - 2;
        int max = indexOfTitle + 2;

        String prefix = "\u3000\u3000"; // 2个空白字符
        String separator = "\n\n";

        SpannableStringBuilder ssb = new SpannableStringBuilder(mText, getStart(), getEnd());
        ssb.clear();

        boolean isEmpty;

        BoundaryString text = new BoundaryString(mText);
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
            boolean canAppend = false;
            boolean shouldConcat = false;
            if (concat) {
                boolean force = this.isForceConcat(str);

                canAppend = force ? force : this.isAppendable(ssb);
                shouldConcat = force ? force : this.isConcatable(str);
                if (i >= min && i <= max) {
                    shouldConcat = false;
                }
            }

            if (canAppend && shouldConcat) {

            } else {

                if (ssb.length() != 0) {
                    ssb.append(separator);
                }

                if (beginSeparator && ssb.length() == 0) {
                    ssb.append('\n');
                }


                // 前置留白
                if (mBook.isArbitrary()) {
                    if (!line.isTitle()) {
                        ssb.append(prefix);
                    }
                } else {
                    if (indexOfTitle >= 0) {
                        if (i > indexOfTitle) {
                            ssb.append(prefix);
                        }
                    }
                }
            }

            // 内容
            if (mBook.isArbitrary()) {

                if (line.isTitle()) {
                    CharSequence ss = this.createTitle(str, false);
                    ssb.append(ss);
                } else {
                    ssb.append(str);
                }

            } else {

                if (i == indexOfTitle) {
                    str = this.getTitle(line);
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

    String getTitle(TextLine line) {
        StringBuilder sb = new StringBuilder(line.mContentLength + 1);

        BoundaryString text = new BoundaryString(line.mBook.mText);
        text.set(line.mBegin, line.mTitleEnd);
        text.trim();
        sb.append(text.toString());

        sb.append('\n');

        text.set(line.mTitleEnd + 1, line.mEnd);
        text.trim();
        sb.append(text.toString());

        return sb.toString();
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

    String lastLine() {
        int size = mList.size();
        for (int i = size - 1; i >= 0; i--) {
            TextLine line = mList.get(i);
            if (!line.isEmpty()) {
                return line.getText();
            }
        }

        return "";
    }

    static boolean isAppendable(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }

        int length = text.length();
        char c = text.charAt(length - 1);

        // 中文结束，可以考虑
        if (ChapterBook.isChinese(c)) {
            return true;
        }

        // 可以连接的符号
        char[] array = APPENDABLE;
        for (char v : array) {
            if (c == v) {
                return true;
            }
        }

        return false;

    }

    static boolean isForceConcat(String text) {

        {
            char c = text.charAt(0);
            char[] array = FORCE;
            for (char v : array) {
                if (c == v) {
                    return true;
                }
            }
        }

        return false;
    }

    static boolean isConcatable(String text) {

        if (text.length() >= 48) {
            return false;
        }

        {
            int count = 0;

            char[] array = CONCATABLE;
            for (char c : array) {
                count += countOf(text, c);
            }

            int length = text.length();
            if (length >= 12) {
                return (count >= 2);
            }

            return (count >= 1);
        }
    }

    static int countOf(String text, char c) {

        int count = 0;

        int index;
        int pos = 0;
        while (true) {
            index = text.indexOf(c, pos);
            if (index < 0) {
                break;
            }

            ++count;
            pos = index + 1;
        }

        return count;
    }

    @Override
    public String toString() {
        return this.getName(false);
    }
}
