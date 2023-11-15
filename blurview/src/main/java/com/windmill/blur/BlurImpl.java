package com.windmill.blur;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * NO OPERATION BlurImpl
 * <p>
 * DO NOT use it directly
 */
public class BlurImpl {
    public static final float DEFAULT_RADIUS = 10;
    public static final float DEFAULT_SCALE = 12;

    /**
     * size of the blur operation for resource reuse or verification
     */
    protected int height, width;
    /**
     * blur radius.
     * <p>
     * <p></p>
     * RenderEffect: radius > 0
     * <p>
     * RenderScript: 0 > radius <= 25
     */
    protected float radius = DEFAULT_RADIUS;
    protected float scale = DEFAULT_SCALE;

    /**
     * copy configuration from another {@link BlurHelper}
     */
    public void from(@NonNull BlurImpl copy) {
        //call methods to avoid skipping necessary initial steps
        setRadius(copy.radius);
        setScale(copy.scale);
        BlurView.log(Log.INFO, "BlurImpl copy from: radius = " + radius + ", scale = " + scale);
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    /**
     * @param bitmap bitmap to be blurred
     * @return blurred bitmap
     */
    @NonNull
    public Bitmap blur(@NonNull Bitmap bitmap) {
        return bitmap;
    }

    /**
     * Frees allocated resources
     */
    public void free() {
    }

    /**
     * @return true if this algorithm returns the same instance of bitmap as it accepted
     * false if it creates a new instance.
     * <p>
     * If you return false from this method, you'll be responsible to swap bitmaps in your
     * {@link BlurImpl#blur(Bitmap)} implementation
     * (assign input bitmap to your field and return the instance algorithm just blurred).
     */
    public boolean multiBitmap() {
        return true;
    }

    /**
     * Retrieve the {@link Bitmap.Config} on which the {@link BlurImpl}
     * can actually work.
     *
     * @return bitmap config supported by the given blur algorithm.
     */
    @NonNull
    public Bitmap.Config bitmapConfig() {
        return Bitmap.Config.ARGB_8888;
    }

    /**
     * set bitmap size scaling factor
     * <p>
     * scale down bitmap to get better performance.
     * the bigger the scale set, the smaller bitmap get
     *
     * @return scale
     */
    public final float getScale() {
        return scale;
    }

    /**
     * get bitmap size scaling factor
     * <p>
     * scale down bitmap to get better performance.
     * the bigger the scale set, the smaller bitmap get
     */
    public void setScale(@FloatRange(from = 1) float scale) {
        this.scale = scale;
    }

    /**
     * blur a bitmap to canvas
     *
     * @param canvas blur bitmap canvas holder
     * @param bitmap bitmap to be blurred
     */
    public void render(@NonNull Canvas canvas, @NonNull Bitmap bitmap) {
    }

    /**
     * identifying what implementation type it used,
     * null if no blur
     *
     * @return type name
     */
    @Nullable
    public String type() {
        return null;
    }

}
