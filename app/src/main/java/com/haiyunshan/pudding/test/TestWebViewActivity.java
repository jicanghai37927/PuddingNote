package com.haiyunshan.pudding.test;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import com.haiyunshan.pudding.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestWebViewActivity extends AppCompatActivity {

    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_web_view);

        this.mWebView = findViewById(R.id.web_view);
        mWebView.loadUrl(getUrl());

        findViewById(R.id.btn_print_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printLayout();
            }
        });
        findViewById(R.id.btn_clipboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printClipboard();
            }
        });
    }

    void printClipboard() {
        boolean withFormatting = true;

        ClipboardManager clipboard =
                (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null) {
            int count = clip.getItemCount();
            Log.w("AA", "count = " + count);

            for (int i = 0; i < clip.getItemCount(); i++) {

                CharSequence paste;
                if (withFormatting) {
                    paste = clip.getItemAt(i).coerceToStyledText(getContext());
                } else {
                    // Get an item as text and remove all spans by toString().
                    final CharSequence text = clip.getItemAt(i).coerceToText(getContext());
                    paste = (text instanceof Spanned) ? text.toString() : text;
                }

                Log.w("AA", "Class = " + paste.getClass().toString());
                if (paste instanceof Spanned) {
                    Spanned ss = (Spanned)paste;
                    Object[] spans = ss.getSpans(0, ss.length(), Object.class);
                    Log.w("AA", "span count = " + spans.length);
                    for (Object s : spans) {
                        int start = ss.getSpanStart(s);
                        int end = ss.getSpanEnd(s);
                        int flag = ss.getSpanFlags(s);
                        Log.w("AA", s.getClass().toString());
                        Log.w("AA", start + ", " + end + ", " + flag);

                        if (s instanceof ImageSpan) {
                            ImageSpan span = (ImageSpan)s;
                            Drawable d = span.getDrawable();
                            int width = d.getIntrinsicWidth();
                            int height = d.getIntrinsicHeight();

                            if (d instanceof BitmapDrawable) {

                                Bitmap bmp = ((BitmapDrawable)d).getBitmap();
                                String name = span.getSource();

                                File file = new File(Environment.getExternalStorageDirectory(), "剪贴板");
                                file.mkdirs();
                                file = new File(file, name);
                                try {
                                    file.createNewFile();
                                    FileOutputStream fos = new FileOutputStream(file);
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                    fos.close();;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                Log.w("AA", width + ", " + height + ", " + span.getSource() + ", " + file.getAbsolutePath());
                            }
                        }
                    }
                }
            }
        }
    }

    Context getContext() {
        return this;
    }

    void printLayout() {
        int count = mWebView.getChildCount();
        Log.w("AA", "child count = " + count);

        for (int i = 0; i < count; i++) {
            View v = mWebView.getChildAt(i);
            Log.w("AA", i + " = " + v.toString());
        }
    }

    String getUrl() {
        File file = Environment.getExternalStorageDirectory();
        file = new File(file, "测试/tt/tt.html");

        String url = Uri.fromFile(file).toString();
        return url;
    }
}
