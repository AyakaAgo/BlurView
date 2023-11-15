package com.windmill.blur;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

/**
 * Scales width and height by [scaleFactor],
 * and then rounds the size proportionally so the width is divisible by [ROUNDING_VALUE]
 */
public final class Sizer {
    /**
     * Bitmap size should be divisible by ROUNDING_VALUE to meet stride requirement.
     * <p>
     * This will help avoiding an extra bitmap allocation when passing the bitmap to RenderScript for blur.
     * <p>
     * Usually it's 16, but on Samsung devices it's 64 for some reason.
     */
    private static final int ROUNDING = 120;
    private final float scale;

    public Sizer(@FloatRange(from = 1) float scale) {
        this.scale = scale;
    }

    @NonNull
    public int[] scale(int width, int height) {
        int nonRoundedScaledWidth = downscaleSize(width);
        //Rounds a value to the nearest divisible to meet stride requirement
        int round = nonRoundedScaledWidth % ROUNDING;
        int scaledWidth = round == 0 ? nonRoundedScaledWidth : nonRoundedScaledWidth - (nonRoundedScaledWidth % ROUNDING) + ROUNDING;
        //Only width has to be aligned to ROUNDING_VALUE
        float roundingScaleFactor = (float) width / scaledWidth;
        //Ceiling because rounding or flooring might leave empty space on the View's bottom
        return new int[]{scaledWidth, (int) Math.ceil(height / roundingScaleFactor)};
    }

    public boolean isInvalid(int measuredWidth, int measuredHeight) {
        return downscaleSize(measuredHeight) == 0 || downscaleSize(measuredWidth) == 0;
    }

    private int downscaleSize(float value) {
        return (int) Math.ceil(value / scale);
    }

}
