package com.windmill.blur;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Choreographer;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

import androidx.annotation.ChecksSdkIntAtLeast;
import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * FrameLayout that blurs its underlying content.
 * Can have children and draw them over blurred background.
 */
public class BlurView extends FrameLayout {
    private static final boolean DEBUG = false;
    private static final String TAG = "BlurView";//.class.getSimpleName();

    @NonNull
    private BlurHelper helper = new BlurHelper();
    private int targetId;
    private boolean targetWithPreDraw;
    private FpsFrameCallback frameCallback;

    public BlurView(Context context) {
        super(context);
    }

    public BlurView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BlurView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    public static boolean canUseRenderEffect() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
    }

    protected static void log(int level, String message) {
        if (DEBUG) {
            Log.println(level, TAG, message);
        }
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BlurView, defStyleAttr, 0);
        int overlayColor = a.getColor(R.styleable.BlurView_overlayColor, Color.TRANSPARENT);
        if (overlayColor != Color.TRANSPARENT) {
            setOverlayColor(overlayColor);
        }
        float blurRadius = a.getFloat(R.styleable.BlurView_blurRadius, 0);
        if (blurRadius > 0) {
            setBlurRadius(blurRadius);
        }
        float blurScale = a.getFloat(R.styleable.BlurView_blurScale, 0);
        if (blurScale >= 1) {
            setBlurScale(blurScale);
        }
        float cornerRadius = a.getFloat(R.styleable.BlurView_cornerRadius, 0);
        if (cornerRadius > 0) {
            setCornerRadius(cornerRadius);
        }
        targetId = a.getResourceId(R.styleable.BlurView_blurTarget, View.NO_ID);
        if (targetId != View.NO_ID) {
            targetWithPreDraw = a.getBoolean(R.styleable.BlurView_blurWithPreDraw, true);
        }
        a.recycle();
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        helper.draw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        helper.update();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        log(Log.INFO, "should stop frameCallback " + (frameCallback != null));
        if (frameCallback != null) {
            frameCallback.stopListening();
        }

        helper.setDynamic(false);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isHardwareAccelerated()) {
            log(Log.INFO, "should start frameCallback " + (frameCallback != null));
            if (frameCallback != null) {
                frameCallback.startListening();
            }

            if (targetId != View.NO_ID) {
                View target = getRootView().findViewById(targetId);
                if (target != null) {
                    with(target, targetWithPreDraw);
                    log(Log.INFO, "init with target id 0x" + Integer.toHexString(targetId) + ", pre draw " + targetWithPreDraw);
                } else {
                    log(Log.ERROR, "blurTarget should have same root view with BlurView");
                }
                targetId = 0;
                return;
            }

            helper.setDynamic(true);
        } else {
            log(Log.WARN, "BlurView can't be used in not hardware-accelerated window!");
        }
    }

    /**
     * @param rootView root to start blur from.
     *                 Can be Activity's root content layout (android.R.id.content)
     *                 or (preferably) some of your layouts. The lower amount of Views are in the root, the better for performance.
     *                 <p>
     *                 BlurImpl is automatically picked based on the API version.
     *                 It uses RenderEffectBlur on API 31+, and RenderScriptBlur on older versions.
     */
    public void with(@NonNull View rootView) {
        with(rootView, true);
    }

    public void with(@NonNull View rootView, boolean onPreDraw) {
        Context context = getContext();
        //noinspection deprecation
        with(rootView, canUseRenderEffect() ? new RenderEffectBlur(context) : new RenderScriptBlur(context), onPreDraw);
    }

    /**
     * @param rootView root to start blur from.
     *                 Can be Activity's root content layout (android.R.id.content)
     *                 or (preferably) some of your layouts. The lower amount of Views are in the root, the better for performance.
     * @param impl     sets the blur impl
     */
    public void with(@NonNull View rootView, @NonNull BlurImpl impl) {
        with(rootView, impl, true);
    }

    public void with(@NonNull View rootView, @NonNull BlurImpl impl, boolean onPreDraw) {
        BlurHelper helper;
        if (onPreDraw) {
            helper = new PreDrawHelper(this, rootView, impl);
        } else {
            helper = new LayoutChangeHelper(this, rootView, impl);
        }
        with(helper);
    }

    public void with(@NonNull BlurHelper helper) {
        with(helper, true);
    }

    public void with(@NonNull BlurHelper helper, boolean copyConfiguration) {
        BlurHelper old = this.helper;
        if (copyConfiguration) {
            helper.from(old);
        }
        old.free();
        this.helper = helper;
    }

    /**
     * a convenient way to set rounded corners.
     * This will replace the previously set {@link ViewOutlineProvider}
     */
    public void setCornerRadius(@FloatRange(from = 0) float radius) {
        if (getOutlineProvider() instanceof RoundedOutline provider) {
            provider.setRadius(radius);
            invalidateOutline();
        } else {
            setOutlineProvider(new RoundedOutline(radius));
        }
        setClipToOutline(radius > 0);
    }

    // Setters duplicated to be able to conveniently change these settings outside of setupWith chain

    /**
     * @see BlurHelper#setRadius(float)
     */
    public void setBlurRadius(float radius) {
        helper.setRadius(radius);
    }

    /**
     * @see BlurHelper#setOverlayColor(int)
     */
    public void setOverlayColor(@ColorInt int overlayColor) {
        helper.setOverlayColor(overlayColor);
    }

    /**
     * @see BlurHelper#setDynamic(boolean)
     */
    public void setBlurDynamic(boolean enabled) {
        helper.setDynamic(enabled);
    }

    /**
     * @see BlurHelper#setEnabled(boolean)
     */
    public void setBlurEnabled(boolean enabled) {
        helper.setEnabled(enabled);
    }

    /**
     * @see BlurHelper#setScale(float)
     */
    public void setBlurScale(@FloatRange(from = 1) float scale) {
        helper.setScale(scale);
    }

    /**
     * @see BlurHelper#getType()
     */
    @Nullable
    public String getBlurType() {
        return helper.getType();
    }

    public void setFpsListener(@Nullable FpsListener listener) {
        if (frameCallback == null) {
            if (listener == null) {
                return;
            }
            frameCallback = new FpsFrameCallback();
        }
        frameCallback.setListener(listener);
        if (listener != null) {
            if (isAttachedToWindow()) {
                frameCallback.startListening();
            }
        } else {
            frameCallback.stopListening();
        }
    }

    private static class RoundedOutline extends ViewOutlineProvider {
        protected float radius;

        protected RoundedOutline(float radius) {
            setRadius(radius);
        }

        protected void setRadius(float radius) {
            this.radius = radius;
        }

        @Override
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
        }

    }

    public static abstract class FpsListener {
        protected void reportFps(float fps, float frameTookMillis) {
            onReportFps(fps, frameTookMillis);
        }

        public abstract void onReportFps(float fps, float frameTookMillis);
    }

    public static abstract class TimePeriodFpsListener extends FpsListener {
        private long lastReportMillis;
        private final long periodMillis;

        public TimePeriodFpsListener(long periodMillis) {
            this.periodMillis = periodMillis;
        }

        protected void reportFps(float fps, float frameTookMillis) {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - lastReportMillis >= periodMillis) {
                onReportFps(fps, frameTookMillis);
                lastReportMillis = currentTimeMillis;
            }
        }
    }

    public static abstract class PerSecondFpsListener extends TimePeriodFpsListener {
        public PerSecondFpsListener() {
            super(1000);
        }
    }

    private static class FpsFrameCallback implements Choreographer.FrameCallback {
        @Nullable
        private FpsListener listener;
        private long lastTimeNanos;
        private boolean listening;

        private void postFrameCallback() {
            Choreographer.getInstance().postFrameCallback(this);
        }

        protected void setListener(@Nullable FpsListener listener) {
            this.listener = listener;
        }

        protected void startListening() {
            if (listener != null && !listening) {
                log(Log.INFO, "frameCallback started.");
                postFrameCallback();
                listening = true;
            }
        }

        protected void stopListening() {
            if (listening) {
                log(Log.INFO, "frameCallback stopped.");
                Choreographer.getInstance().removeFrameCallback(this);
                listener = null;
                listening = false;
            }
        }

        @Override
        public void doFrame(long frameTimeNanos) {
            if (listener == null) {
                return;
            }

            if (lastTimeNanos != 0) {
                float diff = (frameTimeNanos - lastTimeNanos) / 1000000f;
                float fps = 1000f / diff;
                log(Log.INFO, "fps " + fps + ", frameTimeNanos " + frameTimeNanos + ", diff nanos " + diff);
                listener.reportFps(fps, diff);
            }

            lastTimeNanos = frameTimeNanos;
            //continue monitor
            postFrameCallback();
        }
    }

}
