package com.windmill;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import dalvik.annotation.TestTarget;

/**
 * base {@link WebView} blur activity
 * <p></p>
 * <p>
 * <strong>DO NOT</strong> use in production
 */
abstract class BaseWebBlurActivity extends Activity {
    protected static final int blurRadius = 25;
    protected WebView webView;
    protected FrameLayout content;
    protected FrameLayout webViewContainer;

    @SuppressLint({"SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        content = new FrameLayout(this);
        webView = new WebView(this);
        webViewContainer = new FrameLayout(this);

        //simply setup webview
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return !url.startsWith("http");
            }

        });

        webView.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && webView.canGoBack()) {
                webView.goBack();
                return true;
            }
            return false;
        });

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setUseWideViewPort(true);
        settings.setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            settings.setAlgorithmicDarkeningAllowed(true);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            webView.setForceDarkAllowed(true);
        }

        onCreateBlurView();

        webView.loadUrl("https://bing.com");

        webViewContainer.addView(webView);
        content.addView(webViewContainer);

        ViewGroup.LayoutParams layoutParams = webView.getLayoutParams();
        ViewGroup.LayoutParams layoutParams1 = webViewContainer.getLayoutParams();
        layoutParams.width = layoutParams.height = layoutParams1.width = layoutParams1.height = ViewGroup.LayoutParams.MATCH_PARENT;

        setContentView(content);
    }

    /**
     * create blur view before {@link Activity#setContentView(View)}
     */
    protected abstract void onCreateBlurView();

}