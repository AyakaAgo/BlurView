package com.windmill;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.windmill.blur.BlurView;
import com.windmill.ui.UiUtil;

/**
 * simple implementation similar to {@link BlurView}
 */
@RequiresApi(api = Build.VERSION_CODES.S)
public class RawWebViewBlurActivity extends BaseWebBlurActivity {
    /**
     * bitmap holding blur data
     */
    Bitmap capture;

    @SuppressLint("DrawAllocation")
    @Override
    protected void onCreateBlurView() {
        View blurView = new View(this) {
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
        blurView.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), blurRadius);
            }
        });
        blurView.setClipToOutline(true);

        content.addView(blurView);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) blurView.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER;

        capture = Bitmap.createBitmap(
                layoutParams.width = UiUtil.getDpValue(this, 300),
                layoutParams.height = UiUtil.getDpValue(this, 200),
                Bitmap.Config.ARGB_8888
        );
        Canvas captureCanvas = new Canvas();
        captureCanvas.setBitmap(capture);

        webView.getViewTreeObserver().addOnPreDrawListener(() -> {
            captureCanvas.save();
            captureCanvas.translate(-blurView.getLeft(), -blurView.getTop());
            webViewContainer.draw(captureCanvas);
            captureCanvas.restore();
            blurView.invalidate();
            return true;
        });

        Toast.makeText(this, R.string.non_blurview_impl_hint, Toast.LENGTH_SHORT)
                .show();
    }

}