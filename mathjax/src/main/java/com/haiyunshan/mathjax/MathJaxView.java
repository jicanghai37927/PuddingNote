package com.haiyunshan.mathjax;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

public class MathJaxView extends WebView {

    private static final String TAG = MathJaxView.class.getSimpleName();

    private static final String HTML_LOCATION = "file:///android_asset/MathJax/AndroidMathJax.html";

    String mText;

    private Handler handler = new Handler();
    protected MathJaxJavaScriptBridge mBridge;
    private OnMathJaxRenderListener onMathJaxRenderListener;

    private boolean mWebViewLoaded = false;

    public MathJaxView(Context context) {
        this(context, null);
    }

    public MathJaxView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MathJaxView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MathJaxView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.init(context);
    }

    void init(Context context) {

        {
            this.setBackgroundColor(Color.TRANSPARENT);

            // be careful, we do not need internet access
            this.getSettings().setBlockNetworkLoads(true);

            //
            this.getSettings().setLoadWithOverviewMode(true);
            this.getSettings().setJavaScriptEnabled(true);
//            this.getSettings().setUseWideViewPort(true);
        }

        {
            mBridge = new MathJaxJavaScriptBridge(this);
            this.addJavascriptInterface(mBridge, "Bridge");

            MathJaxConfig config = new MathJaxConfig();
            this.addJavascriptInterface(config, "BridgeConfig");
        }

        if (false) {
            // caching
            File dir = context.getCacheDir();
            if (!dir.exists()) {
                Log.d(TAG, "directory does not exist");
                boolean mkdirsStatus = dir.mkdirs();
                if (!mkdirsStatus) {
                    Log.e(TAG, "directory creation failed");
                }
            }
            getSettings().setAppCachePath(dir.getPath());
            getSettings().setAppCacheEnabled(true);
            getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        }

        {
            this.mWebViewLoaded = false;
            this.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);

                    if (mWebViewLoaded) {
                        // WebView was already finished
                        // do not load content again
                        return;
                    }

                    mWebViewLoaded = true;
                    if (!TextUtils.isEmpty(mText)) {
                        setText(mText);
                    }
                }
            });
        }

        {
            this.loadUrl(HTML_LOCATION);
        }
    }

    public void setRenderListener(OnMathJaxRenderListener onMathJaxRenderListener) {
        this.onMathJaxRenderListener = onMathJaxRenderListener;
    }

    public void setText(String text) {
        this.mText = text;

        //wait for WebView to finish loading
        if (!mWebViewLoaded) {
            return;
        }

        String laTexString;
        if (text != null) {
            laTexString = doubleEscapeTeX(text);
        } else {
            laTexString = "";
        }

        String javascriptCommand = "javascript:changeLatexText(\'" + laTexString + "\')";
        this.loadUrl(javascriptCommand);

    }

    /**
     * called when webView is ready with rendering LaTex
     */
    void rendered() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (onMathJaxRenderListener != null)
                    onMathJaxRenderListener.onRendered(MathJaxView.this);
            }
        }, 100);
    }

    private String doubleEscapeTeX(String s) {

        StringBuilder t = new StringBuilder(s.length());

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\'') t.append('\\');
            if (s.charAt(i) == '\\') t.append("\\");
            if (s.charAt(i) != '\n') t.append(s.charAt(i));
        }

        return t.toString();
    }

}
