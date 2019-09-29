package com.haiyunshan.pudding.html;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;

import com.haiyunshan.pudding.chapter.PlainText;

import org.xml.sax.XMLReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HtmlPage {

    ArrayList<BaseDiv> mList;

    File mFile;

    public HtmlPage(File file) {
        this.mFile = file;

        this.mList = new ArrayList<>();
    }

    public List<BaseDiv> getList() {
        return mList;
    }

    public int size() {
        return mList.size();
    }

    public BaseDiv get(int index) {
        return mList.get(index);
    }

    public void inflate() {
        Spanned target;

        //
        PlainText text = new PlainText(mFile);

        //
        String source = text.getText();
        Html.ImageGetter imageGetter = new PageImageGetter();
        Html.TagHandler tagHandler = new PageTagHandler();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            target = Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY, imageGetter, tagHandler);
        } else {
            target = Html.fromHtml(source, imageGetter, tagHandler);
        }

        //
        ArrayList<SpanObject> spans = getImageSpanList(target);
        if (spans == null || spans.size() == 0) {
            ParagraphDiv div = new ParagraphDiv(this, target);
            mList.add(div);
        } else {
            int size = spans.size();

            for (int i = 0; i < size; i++) {
                int start = (i == 0)? 0: spans.get(i - 1).mEnd; // 上一个的结尾
                int end = spans.get(i).mStart; // 当前的开始

                CharSequence cs = target.subSequence(start, end);
                if (cs.length() != 0) {
                    ParagraphDiv div = new ParagraphDiv(this, cs);
                    mList.add(div);
                }

                {
                    String src = spans.get(i).mSpan.getSource();
                    if (!TextUtils.isEmpty(src)) {
                        PictureDiv div = new PictureDiv(this, src);
                        mList.add(div);
                    }
                }
            }

            int last = size - 1;
            if (spans.get(last).mEnd < target.length()) {
                int start = spans.get(last).mEnd;
                int end = target.length();

                CharSequence cs = target.subSequence(start, end);
                if (cs.length() != 0) {
                    ParagraphDiv div = new ParagraphDiv(this, cs);
                    mList.add(div);
                }
            }
        }
    }

    ArrayList<SpanObject> getImageSpanList(Spanned target) {
        ImageSpan[] spans = target.getSpans(0, target.length(), ImageSpan.class);
        if (spans == null || spans.length == 0) {
            return new ArrayList<>();
        }

        ArrayList<SpanObject> list = new ArrayList<>(spans.length);

        {
            int[][] array = new int[spans.length][2];
            for (int i = 0; i < spans.length; i++) {
                ImageSpan s = spans[i];

                int start = target.getSpanStart(s);
                int end = target.getSpanEnd(s);
                array[i][0] = start;
                array[i][1] = end;

                list.add(new SpanObject(s, start, end));
            }
        }

        // 排序之，坚果pro2出现未排序的情况
        {
            Collections.sort(list, new Comparator<SpanObject>() {
                @Override
                public int compare(SpanObject o1, SpanObject o2) {
                    return o1.mStart - o2.mStart;
                }
            });
        }

        return list;
    }

}

class SpanObject {

    ImageSpan mSpan;
    int mStart;
    int mEnd;

    public SpanObject(ImageSpan span, int start, int end) {
        this.mSpan = span;
        this.mStart = start;
        this.mEnd = end;
    }

}

class PageImageGetter implements Html.ImageGetter {

    Drawable mEmptyDrawable = new SourceDrawable();

    @Override
    public Drawable getDrawable(String source) {
        return mEmptyDrawable;
    }
}

class PageTagHandler implements Html.TagHandler {

    int mStart;

    PageTagHandler() {
        this.mStart = -1;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {

        if (tag.equalsIgnoreCase("style")) {
            if (opening) {

                mStart = output.length();

            } else {

                if (mStart >= 0) {
                    output.delete(mStart, output.length());
                }

                mStart = -1;
            }
        }

    }
}

class SourceDrawable extends Drawable {

    @Override
    public void draw(@NonNull Canvas canvas) {

    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}