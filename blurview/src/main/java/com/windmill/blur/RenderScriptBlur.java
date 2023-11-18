package com.windmill.blur;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * Blur using {@link RenderScript}, processed on GPU when device drivers support it.
 * Requires API 17+
 *
 * @deprecated because RenderScript is deprecated and its hardware acceleration is not guaranteed.
 * RenderEffectBlur is the best alternative at the moment.
 */
@SuppressLint("ObsoleteSdkInt")
@Deprecated
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class RenderScriptBlur extends BlurImpl {
    @NonNull
    private final Paint paint = new Paint(/*Paint.FILTER_BITMAP_FLAG*/);
    @NonNull
    private final RenderScript script;
    @NonNull
    private final ScriptIntrinsicBlur blur;
    //@Nullable
    private Allocation outAlloc;

    /**
     * @param context Context to create the {@link RenderScript}
     */
    public RenderScriptBlur(Context context) {
        script = RenderScript.create(context);
        blur = ScriptIntrinsicBlur.create(script, Element.U8_4(script));
        //should be -1 to create a outAlloc Allocation
        //if you make sure you bitmaps are w/h > 0, comment this -1
        height = -1;
    }

    @Override
    public void setRadius(@FloatRange(from = 0, to = 25) float radius) {
        //comment Math.min if your IDE checks it
        super.setRadius(Math.min(25, radius));
    }

    /**
     * @param bitmap bitmap to blur
     * @return blurred bitmap
     */
    @Override
    public @NonNull Bitmap blur(@NonNull Bitmap bitmap) {
        if (radius > 0) {
            //Allocation will use the same backing array of pixels as bitmap if created with USAGE_SHARED flag
            Allocation inAllocation = Allocation.createFromBitmap(script, bitmap);

            if (bitmap.getHeight() != height || bitmap.getWidth() != width) {
                if (outAlloc != null) {
                    outAlloc.destroy();
                }
                outAlloc = Allocation.createTyped(script, inAllocation.getType());
                width = bitmap.getWidth();
                height = bitmap.getHeight();
            }

            //must limit to 25
            blur.setRadius(radius);
            blur.setInput(inAllocation);
            //do not use inAllocation in forEach. it will cause visual artifacts on blurred Bitmap
            blur.forEach(outAlloc);
            outAlloc.copyTo(bitmap);

            inAllocation.destroy();
        }
        return bitmap;
    }

    @Override
    public void free() {
        blur.destroy();
        script.destroy();
        if (outAlloc != null) {
            outAlloc.destroy();
        }
    }

    @Override
    public void render(@NonNull Canvas canvas, @NonNull Bitmap bitmap) {
        canvas.drawBitmap(bitmap, 0f, 0f, paint);
    }

    @Override
    public @Nullable String type() {
        return "RS";
    }

}
