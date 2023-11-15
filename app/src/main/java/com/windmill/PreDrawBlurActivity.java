package com.windmill;

import android.graphics.Color;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.windmill.ui.RoundedOutline;
import com.windmill.ui.UiUtil;
import com.windmill.blur.BlurView;
import com.windmill.blur.PreDrawHelper;

/**
 * a test class for {@link PreDrawHelper}
 * <p>
 * <strong>DO NOT</strong> use it in production environment
 */
public class PreDrawBlurActivity extends BaseWebBlurActivity {

    @Override
    protected void onCreateBlurView() {
        BlurView blurView = new BlurView(this);
        blurView.setElevation(blurRadius);
        blurView.setBlurRadius(blurRadius / 5f);
        blurView.setBackgroundColor(Color.BLUE);
        blurView.setOverlayColor(0X10FF00FF);
        blurView.with(webViewContainer);
        blurView.setOutlineProvider(RoundedOutline.get(blurRadius));
        blurView.setClipToOutline(true);

        content.addView(blurView);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) blurView.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = UiUtil.getDpValue(this, 300);
        layoutParams.height = UiUtil.getDpValue(this, 200);
    }

}