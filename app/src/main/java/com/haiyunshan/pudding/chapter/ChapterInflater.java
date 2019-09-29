package com.haiyunshan.pudding.chapter;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ChapterInflater {

    static final String TAG = "ChapterInflater";

    static final int STEP_CHARS = 50 * 1024;

    ChapterBook mBook;

    char mSeparator;

    ArrayList<TextLine> mTitles;    // 记录需要处理的标题
    ArrayList<TextLine> mLines;     // 记录所有的分行

    int mCursor;                    // 记录已处理完毕的文本位置
    BoundaryString mBounday;

    ConditionSet mCondition;
    ArrayList<TextLine> mTmpList;

    boolean mChapterOnce = false;   // 特殊处理第一章节

    ChapterInflater(ChapterBook book) {
        String text = book.mText;

        this.mBook = book;

        this.mCursor = 0;
        this.mBounday = new BoundaryString(text);

        this.mSeparator = ChapterBook.getSeparator(text);

        // 如果找不到分隔符，直接结束
        if (mSeparator == ChapterBook.NULL_SEPARATOR) {

            this.mCursor = text.length();
            this.mLines = new ArrayList<>();
            mLines.add(new TextLine(book, 0, mCursor, 0));

            book.mChapters = new ArrayList<>();

            Chapter c = new Chapter(book, 11);
            c.mList.addAll(mLines);
            book.mChapters.add(c);

        } else {
            int length = text.length();

            int capacity = length;
            capacity /= 200;
            capacity = (capacity < 10)? 10: capacity;

            this.mLines = new ArrayList<>(capacity);

            capacity /= 200;
            capacity = (capacity < 10)? 10: capacity;

            this.mTitles = new ArrayList<>(capacity);

            this.mCondition = ConditionSet.create(text.length());

            this.mTmpList = new ArrayList<>(capacity);
        }
    }

    boolean isDone() {
        return (mCursor >= mBounday.getString().length());
    }

    void next() {
        if (isDone()) {
            return;
        }

        int lineStart = mLines.size();  // 行开始位置

        // 提取分行
        {
//            long time = System.currentTimeMillis();

            {
                this.retrieveLines(STEP_CHARS);
            }

//            long ellapse = System.currentTimeMillis() - time;
//            Log.w(TAG, "retrieveLines = " + ellapse);
        }

        ArrayList<TextLine> titles = this.mTmpList;     // 新增加的标题
        titles.clear();

        // 提取标题
        {
//            long time = System.currentTimeMillis();

            {
                this.retrieveTitle(lineStart, titles);
            }

//            long ellapse = System.currentTimeMillis() - time;
//            Log.w(TAG, "retrieveTitle = " + ellapse);
        }

        // 过滤没有内容的标题
        {
//            long time = System.currentTimeMillis();

            {
                this.mTitles.addAll(titles);
                titles.clear();

                ArrayList<TextLine> input = mTitles;
                ArrayList<TextLine> output = titles;

                this.filterEmptyTitle(input, output);

                mTitles.clear();
                titles = output;
            }

//            long ellapse = System.currentTimeMillis() - time;
//            Log.w(TAG, "filterEmptyTitle = " + ellapse);
        }

        // 过滤重复关键字的标题
        {
//            long time = System.currentTimeMillis();

            {
                this.mTitles.addAll(titles);
                titles.clear();

                ArrayList<TextLine> input = mTitles;
                ArrayList<TextLine> output = titles;

                this.filterSameTitle(input, output);

                mTitles.clear();
                titles = output;
            }

//            long ellapse = System.currentTimeMillis() - time;
//            Log.w(TAG, "filterSameTitle = " + ellapse);
        }

        // 提取章节
        {
//            long time = System.currentTimeMillis();

            {
                this.retrieveChapters(titles);

                // 保留最后一个标题下次处理
                mTitles.clear();
                if (!mBounday.isEnd()) {
                    if (!titles.isEmpty()) {
                        mTitles.add(titles.get(titles.size() - 1));
                    }
                }
            }

//            long ellapse = System.currentTimeMillis() - time;
//            Log.w(TAG, "retrieveChapters = " + ellapse);
        }

        // 处理第一个章节，字数不够，第二个章节来凑
        if (!mChapterOnce) {
            List<Chapter> list = mBook.mChapters;
            if (list != null && list.size() >= 2) {
                mChapterOnce = true;

                Chapter c1 = list.get(0);
                Chapter c2 = list.get(1);
                if (!c1.beginWithTitle() && c1.length() < 512) {
                    c1.add(c2);
                    list.remove(1);
                }
            }
        }

        // 删除文章前的空白内容
        if (mBounday.isEnd()) {
            ArrayList<Chapter> chapters = mBook.mChapters;
            if (chapters == null) {
                chapters = new ArrayList<>();

                Chapter c = new Chapter(mBook, mLines.size());
                c.mList.addAll(mLines);
                chapters.add(c);

                mBook.mChapters = chapters;
            }

            if (chapters != null) {

                for (Chapter c : chapters) {
                    c.trim();
                }

            }
        }

        if (mBounday.isEnd()) {
            Log.e(TAG, "[Nothing happen]IMPORTANT: chapter number = " + mBook.size());
        }

    }

    void retrieveChapters(List<TextLine> list) {
        if (list.isEmpty()) {
            return;
        }

        if (!mBounday.isEnd() && list.size() == 1) {
            return;
        }

        String text = mBounday.getString();

        ArrayList<Chapter> chapters = mBook.mChapters;
        boolean isNull = (chapters == null);
        if (chapters == null) {
            int capacity = text.length();
            capacity /= 200;
            capacity /= 200;
            capacity = (capacity < 10)? 10: capacity;

            mBook.mChapters = new ArrayList<>(capacity);
            chapters = mBook.mChapters;
        }

        // 添加章节
        {
            // 前
            if (isNull && list.get(0).mIndex != 0) {
                int start = 0;
                int end = list.get(0).mIndex;

                Chapter e = new Chapter(mBook, (end - start + 11));
                for (int j = start; j < end; j++) {
                    e.mList.add(mLines.get(j));
                }

                chapters.add(e);
            }

            // 中
            int last = list.size() - 1;
            for (int i = 0; i < last; i++) {
                int start = list.get(i).mIndex;
                int end = list.get(i + 1).mIndex;

                Chapter e = new Chapter(mBook, (end - start + 11));
                for (int j = start; j < end; j++) {
                    e.mList.add(mLines.get(j));
                }
                chapters.add(e);
            }

            // 后
            if (mBounday.isEnd()) {
                int start = list.get(last).mIndex;
                int end = mLines.size();

                Chapter e = new Chapter(mBook, (end - start + 11));
                for (int j = start; j < end; j++) {
                    e.mList.add(mLines.get(j));
                }
                chapters.add(e);
            }
        }

        // 过滤掉空白章节
        if (true) {
            int start = getStart();
            while (!chapters.isEmpty()) {
                Chapter e = chapters.get(0);
                if (e.getEnd() > start) {
                    break;
                }

                chapters.remove(e);
            }

        }
    }

    void filterSameTitle(ArrayList<TextLine> input, ArrayList<TextLine> output) {

        if (!input.isEmpty()) {
            output.add(input.get(0));
        }

        int size = input.size();
        for (int i = 1; i < size; i++) {
            TextLine pre = output.get(output.size() - 1);
            TextLine line = input.get(i);

            String t1 = pre.getTitle();
            String t2 = line.getTitle();
            if (!t1.equals(t2)) {
                output.add(line);
            } else {
                line.mTitleStart = line.mTitleEnd = -1;
            }
        }

    }

    void filterEmptyTitle(ArrayList<TextLine> input, ArrayList<TextLine> output) {

        int size = input.size();
        if (size == 0) {
            return;
        }

        int last = size - 1;
        int length = (mBounday.isEnd())? size: last;

        for (int i = 0; i < length; i++) {
            TextLine line = input.get(i);

            int begin = line.mIndex;
            int end = (i == last)? mLines.size(): input.get(i + 1).mIndex;

            boolean result = hasContent(begin + 1, end);
            if (result) {
                output.add(line);
            } else {
                line.mTitleStart = line.mTitleEnd = -1;
            }
        }

        // 没有到结束，最后一个标题总是不处理
        if (!mBounday.isEnd()) {
            output.add(input.get(last));
        }
    }

    void retrieveTitle(int fromIndex, ArrayList<TextLine> output) {

        int start = fromIndex;
        int end = mLines.size();

        for (int i = start; i < end; i++) {
            TextLine line = mLines.get(i);
            if (mCondition.accept(line)) {
                output.add(line);
            }
        }

    }

    void retrieveLines(int step) {

        int start = mCursor;
        int end = start;
        int lineNum = mLines.size();
        do {
            end += step;

            mBounday.set(start, end);
            this.retrieveLines(mBounday);

            // 如果已经移动到最后，结束之
            if (mBounday.isEnd()) {
                break;
            }

        } while (lineNum == mLines.size()); // 一定要找到新的分行

        // 更新指针位置
        if (mBounday.isEnd()) {
            mCursor = mBounday.getEnd();
        } else {
            int last = mLines.size() - 1;
            mCursor = mLines.get(last).mEnd;
        }
    }

    void retrieveLines(BoundaryString text) {

        char separator = this.mSeparator;
        int sl = 1;

        int index = 0;
        if (!mLines.isEmpty()) {
            int last = mLines.size() - 1;
            index = mLines.get(last).mIndex;

            index++;
        }

        String str = text.getString();
        int begin = text.getStart();
        while (true) {
            int pos = str.indexOf(separator, begin);

            if (pos >= 0 && pos < text.getEnd()) {
                TextLine line = new TextLine(mBook, begin, pos + sl, index);
                mLines.add(line);

                begin = line.mEnd;

                if (begin == text.getEnd()) {
                    break;
                }

            } else {
                if (text.isEnd()) {
                    TextLine line = new TextLine(mBook, begin, text.getEnd(), index);
                    mLines.add(line);
                }

                break;
            }

            index++;
        }


    }

    int getStart() {
        String text = mBounday.getString();

        int start = 0;
        int end = text.length();

        for (int i = start; i < end; i++) {
            char c = text.charAt(i);
            if (!ChapterBook.isWhitespace(c)) {
                return i;
            }
        }

        return end;
    }

    boolean hasContent(int begin, int end) {
        boolean result = false;

        for (int i = begin; i < end; i++) {
            TextLine line = mLines.get(i);
            if (!line.isEmpty()) {
                result = true;
                break;
            }
        }

        return result;
    }
}
