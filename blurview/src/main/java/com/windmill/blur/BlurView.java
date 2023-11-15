package com.windmill.blur;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.ChecksSdkIntAtLeast;
import androidx.annotation.ColorInt;
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
        setOverlayColor(a.getColor(R.styleable.BlurView_overlayColor, Color.TRANSPARENT));
        setBlurRadius(a.getFloat(R.styleable.BlurView_blurRadius, BlurImpl.DEFAULT_RADIUS));
        setBlurScale(a.getFloat(R.styleable.BlurView_blurScale, BlurImpl.DEFAULT_SCALE));
        targetId = a.getResourceId(R.styleable.BlurView_blurTarget, 0);
        if (targetId != 0) {
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
        helper.setDynamic(false);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isHardwareAccelerated()) {
            if (targetId != 0) {
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
    public void setBlurScale(float scale) {
        helper.setScale(scale);
    }

    /**
     * @see BlurHelper#getType()
     */
    @Nullable
    public String getBlurType() {
        return helper.getType();
    }

}
