package com.haiyunshan.pudding.chapter;

import java.util.ArrayList;

public class ConditionSet {

    int mMin;
    int mMax;

    ArrayList<Condition> mList;

    ConditionSet() {
        this.mMin = 2;
        this.mMax = 2;

        this.mList = new ArrayList<>();
    }

    void add(Condition c) {
        mList.add(c);

        if (c.mMin > this.mMin) {
            this.mMin = c.mMin;
        }

        if (c.mMax > this.mMax) {
            this.mMax = c.mMax;
        }
    }

    boolean accept(TextLine line) {
        int length = line.contentLength();
        if (length > mMax || length < mMin) {
            return false;
        }

        for (Condition c : mList) {
            if (c.accept(line)) {
                return true;
            }
        }

        return false;
    }

    static ConditionSet create(int length) {
        ConditionSet set = new ConditionSet();

        if (true) {
            Condition c = new Condition(
                    // 第
                    new char[] { '\u7b2c' },
                    // 章、回、节、集、卷、品
                    new char[] { '\u7ae0', '\u56de', '\u8282', '\u96c6', '\u5377', '\u54c1' },
                    12,
                    12,
                    3,
                    48,
                    32
            );

            set.add(c);
        }

        if (length <= 5 * 1024 * 1024) {
            Condition c = new Condition(
                    new char[] { '\u9644' },    // 附
                    new char[] { '\u5f55' },    // 录
                    12,
                    6,  // 对齐时，中间会插入字符
                    2,
                    48,
                    32
            );

            set.add(c);
        }

        if (length <= 5 * 1024 * 1024) {
            Condition c = new Condition(
                    new char[] { '\u6954' },    // 楔
                    new char[] { '\u5b50' },    // 子
                    12,
                    6,  // 对齐时，中间会插入字符
                    2,
                    48,
                    32
            );

            set.add(c);
        }

        return set;
    }
}
