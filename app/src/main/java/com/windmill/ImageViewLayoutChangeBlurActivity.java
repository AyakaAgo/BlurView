package com.windmill;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.windmill.blur.BlurView;
import com.windmill.blur.LayoutChangeHelper;

/**
 * test class for {@link LayoutChangeHelper}
 * <br><br>
 * see {@link R.layout#layout_change_layout}
 * for how to initialize directly in xml
 * like {@link BlurView#with(View, boolean)}
 *
 */
public class ImageViewLayoutChangeBlurActivity extends Activity {
    private static final String TAG = "LayoutChangeBlur";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_change_layout);

        ((BlurView) findViewById(R.id.blur_view)).setFpsListener(new BlurView.PerSecondFpsListener() {
            @Override
            public void onReportFps(float fps, float frameTookMillis) {
                Log.i(TAG, "fps " + fps + ", took millis " + frameTookMillis);
            }
        });
    }

}
