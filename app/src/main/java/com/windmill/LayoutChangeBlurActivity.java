package com.windmill;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.windmill.blur.BlurView;
import com.windmill.blur.LayoutChangeHelper;
import com.windmill.ui.UiUtil;

/**
 * test class for {@link LayoutChangeHelper}
 * <p>
 * <strong>DO NOT</strong> use in production
 */
public class LayoutChangeBlurActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout content = new FrameLayout(this);
        FrameLayout blurContent = new FrameLayout(this);
        BlurView blurView = new BlurView(this);
        ImageView im = new ImageView(this);

        int padding = UiUtil.getDpValue(this, 24);
        im.setPadding(padding, padding, padding, padding);
        //im.setBackgroundColor(Color.CYAN);
        im.setImageResource(R.drawable.android_logo);

        blurContent.addView(im);

        //you need to set a non-alpha background if your blur content contains alpha
        //This is the same mechanism as the original #setFrameClearDrawable method
        blurView.setBackgroundColor(Color.WHITE);
        blurView.setBlurRadius(18);
        blurView.setOverlayColor(0X10FF00ff);
        //you can call BlurView#with before or after these configurations
        //BlurView will help you inherit them

        //false means use View#addOnLayoutChangeListener for blur changes
        blurView.with(im, false);

        content.addView(blurContent);
        content.addView(blurView);
        setContentView(content);

        ViewGroup.LayoutParams params = blurContent.getLayoutParams();
        params.width = params.height = MATCH_PARENT;

        FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) blurView.getLayoutParams();
        params2.width = UiUtil.getDpValue(this, 300);
        params2.height = UiUtil.getDpValue(this, 200);
        params2.gravity = Gravity.CENTER;
    }

}
