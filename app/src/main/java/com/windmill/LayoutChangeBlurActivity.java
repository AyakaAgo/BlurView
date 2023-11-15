package com.windmill;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.windmill.blur.BlurView;
import com.windmill.blur.LayoutChangeHelper;

/**
 * test class for {@link LayoutChangeHelper}
 * <p>
 * <strong>DO NOT</strong> use in production
 * <br><br>
 * see {@link R.layout#layout_change_layout}
 * for how to initialize directly in xml
 * like {@link BlurView#with(View, boolean)}
 *
 */
public class LayoutChangeBlurActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_change_layout);
    }

}
