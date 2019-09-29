package com.haiyunshan.pudding.chapter;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ChapterBook {

    static final String TAG = "ChapterBook";
    static final char NULL_SEPARATOR = ' ';

    String mText;
    char mSeparator;
    ArrayList<Chapter> mChapters;

    ChapterInflater mChapterInflater;
    ArbitraryInflater mArbitraryInflater;

    ConditionSet mCondition;
    BoundaryString mBoundary;

    boolean mAutoDetect;

    public ChapterBook(String text) {
        this(text, true);
    }

    public ChapterBook(String text, boolean autoDetect) {
        this.mText = text;
        this.mSeparator = NULL_SEPARATOR;

        this.mBoundary = new BoundaryString(mText);

        this.mAutoDetect = autoDetect;
    }

    public String getText() {
        return mText;
    }

    public List<Chapter> getList() {
        return mChapters;
    }

    public int size() {
        if (mChapters == null) {
            return 0;
        }

        return mChapters.size();
    }

    public Chapter get(int index) {
        return mChapters.get(index);
    }

    public boolean isArbitrary() {
        return (mArbitraryInflater != null);
    }

    public boolean isDone() {
        if (mChapters == null || mChapters.isEmpty()) {
            return false;
        }

        Chapter last = mChapters.get(mChapters.size() - 1);
        return (last.getEnd() >= mText.length());
    }

    public void next() {
        if (isDone()) {
            return;
        }

        // 任意分段
        {
            if (mArbitraryInflater != null) {
                mArbitraryInflater.next();
                return;
            }
        }

        // 智能分段
        {
            if (mChapterInflater == null) {
                mChapterInflater = new ChapterInflater(this);
            }

            mChapterInflater.next();

            if (!mChapterInflater.isDone()) {

                // 禁用章节检测
                if (!mAutoDetect) {
                    if (mChapters != null) {
                        mChapters.clear();
                    }
                }

                // 第一次分段后，没有出现章节，切换到任意分段
                if (mChapters == null || mChapters.size() <= 2) {

                    // 清除旧数据，重新来过
                    if (mChapters != null) {
                        mChapters.clear();
                    }

                    this.mArbitraryInflater = new ArbitraryInflater(mChapterInflater);
                    mArbitraryInflater.next();
                }
            }
        }
    }

    public void inflate() {
        ArrayList<TextLine> lines = null;
        ArrayList<TextLine> filter;

        if (TextUtils.isEmpty(mText)) {

            this.mChapters = new ArrayList<>();

        } else {

            {
                long time = System.currentTimeMillis();

                {
                    lines = this.getLines(mText);
                }

                long ellapse = System.currentTimeMillis() - time;
                Log.w(TAG, "getLines = " + ellapse);
            }

            {
                long time = System.currentTimeMillis();

                {
                    this.mCondition = ConditionSet.create(mText.length());
                    filter = this.filterLines(mText, lines, true, true);
                }

                long ellapse = System.currentTimeMillis() - time;
                Log.w(TAG, "filterLines = " + ellapse);
            }

            {
                long time = System.currentTimeMillis();

                {
                    this.mChapters = this.getChapters(mText, lines, filter, true);
                }

                long ellapse = System.currentTimeMillis() - time;
                Log.w(TAG, "getChapters = " + ellapse);
            }

        }

        // 随意断章
        if (mChapters.isEmpty()) {
            Chapter c = new Chapter(this, 11);
            if (lines != null) {
                c.mList.addAll(lines);
            }

            mChapters.add(c);
        }

//        for (Chapter e : mChapters) {
//            Log.w("AA", "Chapter = " + e.getDesc());
//        }
//
        Log.w(TAG, "chapters result = " + mChapters.size());
    }

    ArrayList<Chapter> getChapters(String text, List<TextLine> all, List<TextLine> list, boolean filter) {
        ArrayList<Chapter> chapters = new ArrayList<>(list.size() + 2);

        if (list.isEmpty()) {
            return chapters;
        }

        // 添加章节
        {
            // 前
            if (list.get(0).mIndex != 0) {
                int start = 0;
                int end = list.get(0).mIndex;

                Chapter e = new Chapter(this, (end - start + 11));
                for (int j = start; j < end; j++) {
                    e.mList.add(all.get(j));
                }

                chapters.add(e);
            }

            // 中
            int last = list.size() - 1;
            for (int i = 0; i < last; i++) {
                int start = list.get(i).mIndex;
                int end = list.get(i + 1).mIndex;

                Chapter e = new Chapter(this, (end - start + 11));
                for (int j = start; j < end; j++) {
                    e.mList.add(all.get(j));
                }
                chapters.add(e);
            }

            // 后
            {
                int start = list.get(last).mIndex;
                int end = all.size();

                Chapter e = new Chapter(this, (end - start + 11));
                for (int j = start; j < end; j++) {
                    e.mList.add(all.get(j));
                }
                chapters.add(e);
            }
        }

        // 过滤掉空白章节
        if (filter) {
            int start = getStart();
            while (!chapters.isEmpty()) {
                Chapter e = chapters.get(0);
                if (e.getEnd() > start) {
                    break;
                }

                chapters.remove(e);
            }

        }

        return chapters;
    }

    ArrayList<TextLine> filterLines(String text, List<TextLine> lines, boolean filterEmpty, boolean filterSame) {
        ArrayList<TextLine> list = new ArrayList<>();

        // 过滤出标题
        for (TextLine line : lines) {
            if (mCondition.accept(line)) {
                list.add(line);
            }
        }

        // 过滤没有内容的标题
        if (filterEmpty) {
            ArrayList<TextLine> array = new ArrayList<>(list.size());

            int size = list.size();
            int last = size - 1;
            for (int i = 0; i < size; i++) {
                TextLine line = list.get(i);

                int begin = line.mIndex;
                int end = (i == last)? lines.size(): list.get(i + 1).mIndex;

                boolean result = hasContent(lines, begin + 1, end);
                if (result) {
                    array.add(line);
                }
            }

            list = array;
        }

        // 过滤出相同关键字
        if (filterSame) {

            ArrayList<TextLine> array = new ArrayList<>(list.size());
            if (!list.isEmpty()) {
                array.add(list.get(0));
            }

            int size = list.size();
            for (int i = 1; i < size; i++) {
                TextLine pre = array.get(array.size() - 1);
                TextLine line = list.get(i);

                String t1 = pre.getTitle();
                String t2 = line.getTitle();
                if (!t1.equals(t2)) {
                    array.add(line);
                }
            }

            list = array;
        }

        return list;
    }

    ArrayList<TextLine> getLines(String text) {

        char separator;

        // 获取分隔符
        {
            this.mSeparator = getSeparator(text);
            separator = mSeparator;
        }

        int length = text.length();

        int capacity = length;
        capacity /= 200;
        capacity = (capacity < 10)? 10: capacity;

        ArrayList<TextLine> list = new ArrayList<>(capacity);

        // 执行分行
        if (separator == NULL_SEPARATOR) {
            list.add(new TextLine(this, 0, length, 0));
        } else {

            int sl = 1;

            int index = 0;

            int begin = 0;
            while (true) {
                int pos = text.indexOf(separator, begin);

                if (pos >= 0) {
                    TextLine line = new TextLine(this, begin, pos + sl, index);
                    list.add(line);

                    begin = line.mEnd;

                    if (begin == text.length()) {
                        break;
                    }

                } else {
                    TextLine line = new TextLine(this, begin, text.length(), index);
                    list.add(line);

                    break;
                }

                index++;
            }
        }

        return list;
    }

    boolean hasContent(List<TextLine> lines, int begin, int end) {
        boolean result = false;

        for (int i = begin; i < end; i++) {
            TextLine line = lines.get(i);
            if (!line.isEmpty()) {
                result = true;
                break;
            }
        }

        return result;
    }

    int getStart() {
        String text = this.mText;

        int start = 0;
        int end = text.length();

        for (int i = start; i < end; i++) {
            char c = text.charAt(i);
            if (!isWhitespace(c)) {
                return i;
            }
        }

        return end;
    }

    static char getSeparator(String text) {
        char separator = ' ';

        int newLinePos = text.indexOf('\n');
        int charReturnPos = text.indexOf('\r');
        if (newLinePos >= 0 && charReturnPos >= 0) {
            separator = '\n';
        } else if (newLinePos >= 0 && charReturnPos < 0) {

            separator = '\n';

        } else if (newLinePos < 0 && charReturnPos >= 0) {
            separator = '\r';
        }

        return separator;
    }

    static String trim(String str) {
        int start = str.length();
        int end = 0;
        int length = str.length();

        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            if (!isWhitespace(c)) {
                start = i;
                break;
            }
        }

        for (int i = length - 1; i >= 0; i--) {
            char c = str.charAt(i);
            if (!isWhitespace(c)) {
                end = i + 1;
                break;
            }
        }

        if (start >= end) {
            return "";
        }

        return str.substring(start, end);
    }

    public static boolean isWhitespace(char c) {
        return c == ' '
                || c == '\u3000'
                || c == 0xA0 // macos将rtfd转html后，出现的空白字符
                || c == '\r'
                || c == '\n';
    }

    static boolean isChinese(char c) {
        if (c < '\u4e00' || c > '\u9fa5') { // 中文范围
            return false;
        }
        return true;
    }

}
