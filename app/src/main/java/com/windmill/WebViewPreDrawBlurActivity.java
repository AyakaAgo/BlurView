package com.windmill;

import android.util.Log;

import com.windmill.blur.BlurView;

public class WebViewPreDrawBlurActivity extends WebViewBlurActivity {
    private static final String TAG = "PreDrawBlur";

    @Override
    protected boolean useOnPreDraw() {
        return true;
    }

    @Override
    protected void onCreateBlurView() {
        super.onCreateBlurView();

        blurView.setFpsListener(new BlurView.PerSecondFpsListener() {
            @Override
            public void onReportFps(float fps, float frameTookMillis) {
                Log.i(TAG, "fps " + fps + ", took millis " + frameTookMillis);
            }
        });
    }
}
