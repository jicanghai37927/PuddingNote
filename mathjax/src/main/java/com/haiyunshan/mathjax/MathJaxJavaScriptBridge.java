package com.haiyunshan.mathjax;

import android.webkit.JavascriptInterface;

class MathJaxJavaScriptBridge {

    MathJaxView mOwner;

    public MathJaxJavaScriptBridge(MathJaxView owner){
        this.mOwner = owner;
    }

    @JavascriptInterface
    public void rendered(){
        mOwner.rendered();
    }
}
