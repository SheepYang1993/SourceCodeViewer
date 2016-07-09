package com.sheepyang.sourceviewer.webview;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by SheepYang on 2016/6/2 22:09.
 */
public class MyWebViewClient extends WebViewClient{

    //当内核开始加载访问的url时调用
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    //当重定向时调用
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return super.shouldOverrideUrlLoading(view, url);
    }
}
