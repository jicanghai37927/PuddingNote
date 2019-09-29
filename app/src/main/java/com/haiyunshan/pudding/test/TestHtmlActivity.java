package com.haiyunshan.pudding.test;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.haiyunshan.pudding.R;

import org.apache.commons.io.FileUtils;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.IOException;

public class TestHtmlActivity extends AppCompatActivity {

    TextView mHtmlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_html);

        this.mHtmlView = findViewById(R.id.tv_html);
        CharSequence ss = this.getText();
        ss = Html.fromHtml(getText(), null, new HtmlTagHandler());


        mHtmlView.setText(ss);

        findViewById(R.id.btn_print_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printInfo();
            }
        });
    }

    void printInfo() {
        CharSequence paste = mHtmlView.getText();

        Log.w("AA", "Class = " + paste.getClass().toString());
        if (paste instanceof Spanned) {
            Spanned ss = (Spanned) paste;
            Object[] spans = ss.getSpans(0, ss.length(), Object.class);
            Log.w("AA", "span count = " + spans.length);
            for (Object s : spans) {
                int start = ss.getSpanStart(s);
                int end = ss.getSpanEnd(s);
                int flag = ss.getSpanFlags(s);
                Log.w("AA", s.getClass().toString());
                Log.w("AA", start + ", " + end + ", " + flag);

                if (s instanceof ImageSpan) {
                    ImageSpan span = (ImageSpan) s;
                    Drawable d = span.getDrawable();
                    int width = d.getIntrinsicWidth();
                    int height = d.getIntrinsicHeight();

                    if (d instanceof BitmapDrawable) {

                        Bitmap bmp = ((BitmapDrawable) d).getBitmap();
                        String name = span.getSource();

                        File file = new File(Environment.getExternalStorageDirectory(), "剪贴板");
                        file.mkdirs();
                        file = new File(file, name);
//                        try {
//                            file.createNewFile();
//                            FileOutputStream fos = new FileOutputStream(file);
//                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                            fos.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }

                        Log.w("AA", width + ", " + height + ", " + span.getSource() + ", " + file.getAbsolutePath());
                    }
                }
            }
        }
    }

    String getText() {
        File file = Environment.getExternalStorageDirectory();
        file = new File(file, "测试/tt/tt.html");

        try {
            String text = FileUtils.readFileToString(file, "utf-8");
            return text;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    private class HtmlTagHandler implements Html.TagHandler {

        int mStart;
        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            Log.w("AA", tag + " = " + opening + ", " + output.length());

            if (tag.equalsIgnoreCase("style")) {

                if (opening) {
                    mStart = output.length();
                } else {
                    output.delete(mStart, output.length());
                }
            }

        }
    }
}
