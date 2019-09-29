package com.haiyunshan.pudding.chapter;

import java.util.Arrays;

public class Condition {

    char[] mStart;  // 起始关键字
    char[] mEnd;    // 结束关键字

    int mOffset;    // 起始关键字出现位置
    int mLength;    // 结束关键字出现位置

    int mMin;       // 最小长度
    int mMax;       // 最大长度
    int mCount;     // 文本有效长度

    int[] mBounds;

    public Condition(char[] start, char[] end, int offset, int length, int min, int max, int count) {
        this.mStart = start;
        this.mEnd = end;

        this.mOffset = offset;
        this.mLength = length;

        this.mMin = min;
        this.mMax = max;
        this.mCount = count;

        this.mBounds = new int[2];
    }

    boolean accept(TextLine line) {
        BoundaryString text = line.mBook.mBoundary;

        int[] pos = this.mBounds;

        // 太长或太短，都不视为标题
        int length = line.contentLength();
        if (length > this.mMax || length < this.mMin) {
            return false;
        }

        // 去掉前置的空白，重新测量
        int start = line.getStart(true);
        int end;
        length = line.mContentLength - (start - line.mBegin);
        if (length > mCount || length < this.mMin) {
            return false;
        }

        // 开始关键字匹配

        Arrays.fill(mBounds, -1);

        // 开始位置
        {
            end = start + mOffset;
            end = (end > line.mEnd)? line.mEnd: end; // 不允许越过边界

            text.set(start, end); // 限定搜索范围

            char[] chars = mStart;
            for (char c : chars) {
                int index = text.indexOf(c);
                if (index >= 0) {

                    if (index == line.mBegin) {
                        pos[0] = index;

                    } else {

                        char tmp = text.getString().charAt(index - 1);
                        if (!ChapterBook.isChinese(tmp)) { // 第一个字符前，不允许再出现中文
                            pos[0] = index;
                        }

                    }

                    break;
                }
            }
        }

        // 找不到开始位置，不视为标题
        if (pos[0] < 0) {
            return false;
        }

        // 结束位置
        {
            start = pos[0] + 1;

            end = start + mLength;
            end = (end > line.mEnd)? line.mEnd: end; // 不允许越过边界

            text.set(start, end); // 限定搜索范围

            char[] chars = mEnd;
            for (char c : chars) {
                int index = text.indexOf(c);
                if (index >= 0) {
                    pos[1] = index;
                    break;
                }
            }
        }

        // 找不到结束位置，或者离得太远，不视为标题
        if (pos[1] < 0) {
            return false;
        }

        // 判断是否以中文结束
        if (true) {
            int index = line.getEnd(true);
            index -= 1;
            if (index >= 0 && index < text.getString().length()) {
                char c = text.getString().charAt(index);
                // '\uff0c' --> '，'
                // '\u3002' --> '。'

//                if (c < '\u4e00' || c > '\u9fa5') { // 中文范围
//                    return false;
//                }

                // 不能接受'，'、'。'结尾的标题
                if (c == '\uff0c' || c == '\u3002') {
                    return false;
                }

            }
        }

        line.mTitleStart = pos[0];
        line.mTitleEnd = pos[1] + 1;

        return true;
    }
}
