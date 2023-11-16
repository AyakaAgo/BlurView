package com.windmill;

import android.graphics.Color;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.windmill.ui.UiUtil;
import com.windmill.blur.BlurView;
import com.windmill.blur.PreDrawHelper;

/**
 * test class for {@link PreDrawHelper}
 */
abstract class WebViewBlurActivity extends BaseWebBlurActivity {
    protected BlurView blurView;

    protected abstract boolean useOnPreDraw();

    @Override
    protected void onCreateBlurView() {
        blurView = new BlurView(this);
        blurView.setElevation(blurRadius);
        blurView.setBlurRadius(blurRadius / 5f);
        blurView.setBackgroundColor(Color.BLUE);
        blurView.setOverlayColor(0X10FF00FF);
        blurView.with(webViewContainer, useOnPreDraw());
        blurView.setCornerRadius(blurRadius);

        content.addView(blurView);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) blurView.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = UiUtil.getDpValue(this, 300);
        layoutParams.height = UiUtil.getDpValue(this, 200);
    }

}