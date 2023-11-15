package com.windmill;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.windmill.ui.RoundedOutline;
import com.windmill.ui.UiUtil;
import com.windmill.blur.PreDrawHelper;

/**
 * a test class for {@link PreDrawHelper}
 * <p>
 * <strong>DO NOT</strong> use it in production environment
 */
@RequiresApi(api = Build.VERSION_CODES.S)
public class RawRenderEffectBlurBlurActivity extends BaseWebBlurActivity {
    /**
     * bitmap holding blur data
     */
    private Bitmap capture;
    private Canvas captureCanvas;
    private View blurView;

    @SuppressLint("DrawAllocation")
    @Override
    protected void onCreateBlurView() {
        blurView = new View(this) {
            private final Paint paint = new Paint();

            @Override
            protected void onDraw(@NonNull Canvas canvas) {
                if (capture != null) {
                    canvas.drawBitmap(capture, 0, 0, paint);
                }
            }

        };
        blurView.setElevation(blurRadius);
        blurView.setRenderEffect(RenderEffect.createBlurEffect(blurRadius, blurRadius, Shader.TileMode.CLAMP));
        blurView.setOutlineProvider(RoundedOutline.get(blurRadius));
        blurView.setClipToOutline(true);

        content.addView(blurView);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) blurView.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER;

        capture = Bitmap.createBitmap(
                layoutParams.width = UiUtil.getDpValue(this, 300),
                layoutParams.height = UiUtil.getDpValue(this, 200),
                Bitmap.Config.ARGB_8888
        );
        captureCanvas = new Canvas();
        captureCanvas.setBitmap(capture);

        webView.getViewTreeObserver().addOnPreDrawListener(() -> {
            captureCanvas.save();
            captureCanvas.translate(-blurView.getLeft(), -blurView.getTop());
            webViewContainer.draw(captureCanvas);
            captureCanvas.restore();
            blurView.invalidate();
            return true;
        });
    }

}