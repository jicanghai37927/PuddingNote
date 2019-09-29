package com.haiyunshan.pudding.code;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import com.haiyunshan.pudding.chapter.PlainText;
import com.haiyunshan.pudding.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import prettify.PrettifyParser;
import prettify.parser.Prettify;
import prettify.theme.ThemeSonsOfObsidian;
import syntaxhighlight.ParseResult;
import syntaxhighlight.Style;
import syntaxhighlight.Theme;

public class CodePage {

    File mFile;

    String mContent;

    Theme mTheme;
    List<ParseResult> mList;
    int[] mBreaks;

    public CodePage(File file) {
        this(file, new ThemeSonsOfObsidian());
    }

    public CodePage(File file, Theme theme) {
        this.mFile = file;
        this.mTheme = theme;
    }

    public int getLineCount() {
        return (mBreaks.length + 1);
    }

    public void load() {
        if (mContent != null) {
            return;
        }

        // 加载文本
        {
            PlainText text = new PlainText(mFile);
            this.mContent = text.getText();
        }

        // 记录行位置
        {
            ArrayList<Integer> list = new ArrayList<>(800);

            String str = this.mContent;

            char c = '\n';
            int pos = 0;
            while (true) {
                pos = str.indexOf(c, pos);
                if (pos < 0) {
                    break;
                }

                list.add(pos);

                pos += 1;
            }

            int size = list.size();
            this.mBreaks = new int[size];
            for (int i = 0; i < size; i++) {
                mBreaks[i] = list.get(i);
            }
        }
    }

    public void prettify() {
        if (this.mList != null) {
            return;
        }

        this.load();

        String fileExtension = Utils.getExtension(mFile);
        String content = this.mContent;

        PrettifyParser parser = new PrettifyParser();
        List<ParseResult> results = parser.parse(fileExtension, content);

        // 剔除puctuation、plain，没必要Span
        {
            int size = results.size();
            for (int i = (size - 1); i >= 0; i--) {
                ParseResult r = results.get(i);
                String key = r.getStyleKeys().get(0);
                if (key.equalsIgnoreCase(Prettify.PR_PUNCTUATION)) {
                    results.remove(i);
                    continue;
                }

                if (key.equalsIgnoreCase(Prettify.PR_PLAIN)) {
                    results.remove(i);
                    continue;
                }
            }
        }

        // 排个序
        {
            Collections.sort(results, new Comparator<ParseResult>() {
                @Override
                public int compare(ParseResult o1, ParseResult o2) {
                    return (o1.getOffset() - o2.getOffset());
                }
            });
        }

        this.mList = results;
    }

    public void bind(View container, View gutterView, TextView lineView, TextView codeView) {
        this.load();

        Theme theme = this.mTheme;

        // 编辑器
        {
            container.setBackgroundColor(theme.getBackground().getRGB());
        }

        // 代码区
        {
            codeView.setHorizontallyScrolling(true);
            codeView.setTextColor(theme.getPlain().getColor().getRGB());
            codeView.setHighlightColor(theme.getHighlightedBackground().getRGB());
//            codeView.setMovementMethod(ScrollingMovementMethod.getInstance()); // 不可设置，否则不能选择

            String content = this.mContent;
            codeView.setText(content, TextView.BufferType.SPANNABLE);
        }

        // 行数
        {
            lineView.setTextColor(theme.getPlain().getColor().getRGB());

            int count = this.getLineCount();
            String text = String.valueOf(count);
            TextPaint paint = lineView.getPaint();
            float value = paint.measureText(text);
            int width = (int)(Math.ceil(value));
            width += (lineView.getPaddingLeft() + lineView.getPaddingRight());

            lineView.getLayoutParams().width = width;

            String content = this.getLine();
            lineView.setText(content);

        }

        // Gutter
        {
            gutterView.setBackgroundColor(theme.getGutterBorderColor().getRGB());
        }

    }

    public void makeup(TextView codeView) {
        this.prettify();

        this.makeupAll(codeView);
    }

    void makeupAll(TextView codeView) {
        CharSequence cs = codeView.getText();
        if (!(cs instanceof Spannable)) {
            return;
        }

        Spannable spannable = (Spannable)cs;

        for (ParseResult r : mList) {
            String key = r.getStyleKeys().get(0);
            Style s = mTheme.getStyle(key);
            if (s == null) {
                continue;
            }

            int start = r.getOffset();
            int end = start + r.getLength();
            int flags = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE;

            // 文本色
            {
                int color = s.getColor().getRGB();
                ForegroundColorSpan span = new ForegroundColorSpan(color);
                spannable.setSpan(span, start, end, flags);
            }

            // 加粗、斜体
            {
                int style = 0;
                if (s.isBold() && s.isItalic()) {
                    style = Typeface.BOLD_ITALIC;
                } else if (s.isBold()) {
                    style = Typeface.BOLD;
                } else if (s.isItalic()) {
                    style = Typeface.ITALIC;
                }

                if (style != 0) {
                    StyleSpan span = new StyleSpan(style);
                    spannable.setSpan(span, start, end, flags);
                }
            }

            // 下划线
            if (s.isUnderline()) {
                UnderlineSpan span = new UnderlineSpan();
                spannable.setSpan(span, start, end, flags);
            }
        }
    }

    String getLine() {
        int count = this.getLineCount();
        StringBuilder sb = new StringBuilder(count * 4);

        for (int i = 1; i <= count; i++) {
            sb.append(i);
            sb.append('\n');
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }
}
