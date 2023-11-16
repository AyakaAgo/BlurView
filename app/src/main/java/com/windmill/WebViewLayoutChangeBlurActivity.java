package com.windmill;

public class WebViewLayoutChangeBlurActivity extends WebViewBlurActivity {
    @Override
    protected boolean useOnPreDraw() {
        return false;
    }
}
