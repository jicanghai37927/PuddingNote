package com.haiyunshan.pudding.chapter;

import java.util.ArrayList;

public class ArbitraryInflater {

    static final String TAG = "ArbitraryInflater";

    static final int STEP_CHARS = 20 * 1024;
    static final int CHAPTER_SIZE = 8 * 1024;
    static final int MIN_SIZE = 2 * 1024; // 最后一段，不少于这个字数

    ChapterBook mBook;

    char mSeparator;

    int mLineCursor;
    ArrayList<TextLine> mLines;     // 记录所有的分行

    int mCursor;                    // 记录已处理完毕的文本位置
    BoundaryString mBounday;

    ConditionSet mCondition;
    ArrayList<TextLine> mTmpList;

    ArbitraryInflater(ChapterInflater inflater) {

        this.mBook = inflater.mBook;
        if (mBook.mChapters == null) {
            mBook.mChapters = new ArrayList<>();
        }

        this.mSeparator = inflater.mSeparator;

        this.mLineCursor = 0;
        this.mLines = inflater.mLines;

        this.mCursor = inflater.mCursor;
        this.mBounday = inflater.mBounday;

        mCondition = inflater.mCondition;
        this.mTmpList = inflater.mTmpList;

        if (mCondition != null) {
            int start = 0;
            int end = mLines.size();
            for (int i = start; i < end; i++) {
                TextLine line = mLines.get(i);
                mCondition.accept(line);
            }
        }

    }

    boolean isDone() {
        if (mLines == null) {
            return true;
        }

        boolean lineEnd = (mLineCursor >= mLines.size());
        boolean cursorEnd = (mCursor >= mBounday.getString().length());

        return (cursorEnd && lineEnd);
    }

    void next() {
        if (isDone()) {
            return;
        }

        // Arbitrary从Chapter创建，自带内容，每次先处理上次的数据

        while (true) {

            int max = CHAPTER_SIZE;
            int chapterSize = mBook.size();
            boolean hasRetrieveLines = false;

            int wordCount = 0;
            Chapter c = new Chapter(mBook, 113);

            int start = mLineCursor;
            int end;

            // 循环内，至少添加一个标题直到结束
            while (true) {

                end = mLines.size();
                for (int i = start; i < end; i++) {
                    TextLine line = mLines.get(i);

                    int length = line.wordLength();
                    if (length == 0) {
                        c.mList.add(line);

                        continue;
                    }

                    wordCount += line.wordLength();
                    if (wordCount < max) {
                        c.mList.add(line);

                        continue;
                    }

                    {
                        String last = c.lastLine();
                        String current = line.getText();

                        boolean force = Chapter.isForceConcat(current);
                        boolean appendable = force? force: Chapter.isAppendable(last);
                        boolean concatable = force? force: Chapter.isConcatable(current);
                        if (appendable && concatable) {

                            c.mList.add(line);

                        } else {
                            int remain = (mBounday.getString().length() - c.getEnd());

                            if (remain < MIN_SIZE) { // 剩余内容太少，合并之
                                c.mList.add(line);
                            } else {

                                mBook.mChapters.add(c);

                                c = null;
                                wordCount = 0;
                                mLineCursor = i; // 记录位置

                                // 留给下一次来处理
                                if (hasRetrieveLines) {
                                    break;
                                }

                                // 新起一章
                                c = new Chapter(mBook, 113);
                                c.mList.add(line);
                            }
                        }

                    }
                }

                start = end;

                // 全部结束了
                if (endOfText()) {
                    break;
                }

                // 章节已发生变化，并且已更新过一次数据，留给下一次处理
                if (chapterSize != mBook.size() && hasRetrieveLines) {
                    break;
                }

                this.retrieveLines(STEP_CHARS);
                hasRetrieveLines = true;
            }


            if (endOfText()) {
                if (c != null) {
                    mBook.mChapters.add(c);
                    mLineCursor = mLines.size();
                }
            }

            break;
        }

    }

    boolean endOfText() {
        return (mCursor >= mBounday.getString().length());
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

        // 生成标题
        if (mCondition != null) {
            start = lineNum;
            end = mLines.size();
            for (int i = start; i < end; i++) {
                TextLine line = mLines.get(i);
                mCondition.accept(line);
            }
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


}
