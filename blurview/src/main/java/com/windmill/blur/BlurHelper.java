package com.windmill.blur;

import android.graphics.Canvas;
import android.util.Log;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * NO OPERATION BlurHelper
 * <p>
 * DO NOT use it directly
 */
public class BlurHelper {
    @NonNull
    protected BlurImpl impl;
    @ColorInt
    protected int overlayColor;
    protected boolean enabled = true;

    public BlurHelper() {
        impl = new BlurImpl();
    }

    /**
     * copy configuration from another {@link BlurHelper}
     */
    public void from(@NonNull BlurHelper copy) {
        //call methods to avoid skipping necessary initial steps
        setEnabled(copy.enabled);
        setOverlayColor(copy.overlayColor);
        BlurView.log(Log.INFO, "BlurHelper copy from: enabled = " + enabled + ", overlayColor = " + overlayColor);
        impl.from(copy.impl);
    }

    /**
     * Enable/disables the blur. Enabled by default
     *
     * @param enabled true to enable, false otherwise
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Can be used to stop blur auto update or resume if it was stopped before.
     * Enabled by default.
     */
    public void setDynamic(boolean enabled) {

    }

    /**
     * set bitmap size scaling factor
     * <p>
     * <p></p>
     * scale down bitmap to get better performance.
     * the bigger the scale set, the smaller bitmap get
     */
    public void setScale(@FloatRange(from = 1) float scale) {
        impl.setScale(scale);
    }

    /**
     * @param radius sets the blur radius
     *               Default value is {@link BlurImpl#DEFAULT_RADIUS}
     */
    public void setRadius(@FloatRange(from = 0) float radius) {
        impl.setRadius(radius);
    }

    /**
     * Sets the color overlay to be drawn on top of blurred content,
     * the color should have transparency
     *
     * @param overlayColor int color
     */
    public void setOverlayColor(@ColorInt int overlayColor) {
        this.overlayColor = overlayColor;
    }

    /**
     * Draws blurred content on given canvas
     */
    public void draw(@NonNull Canvas canvas) {
    }

    /**
     * Must be used to notify Controller when content's size has changed
     */
    public void update() {
    }

    /**
     * Frees allocated resources
     */
    public void free() {
    }

    /**
     * @see BlurImpl#type()
     */
    @Nullable
    public String getType() {
        return impl.type();
    }

}
