package com.windmill.blur;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RenderEffect;
import android.graphics.RenderNode;
import android.graphics.Shader;
import android.os.Build;
import android.util.Log;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * Leverages the new {@link RenderEffect#createBlurEffect(float, float, Shader.TileMode)} API to perform blur.
 * Hardware accelerated.
 * Blur is performed on a separate thread - native RenderThread.
 * It doesn't block the Main thread, however it can still cause an FPS drop,
 * because it's just in a different part of the rendering pipeline.
 */
@RequiresApi(api = Build.VERSION_CODES.S)
public class RenderEffectBlur extends BlurImpl {
    @NonNull
    private final RenderNode node = new RenderNode("blur");
    @NonNull
    private final Context context;
    @Nullable
    private BlurImpl fallImpl;
    @Nullable
    private String type/* = "RE"*/;

    public RenderEffectBlur(@NonNull Context context) {
        this.context = context;
    }

    @Override
    public void setRadius(@FloatRange(from = 0) float radius) {
        //comment Math.min if your IDE checks it
        radius = Math.max(0, radius);
        if (this.radius != radius) {
            super.setRadius(radius);
            node.setRenderEffect(RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.CLAMP));
            if (fallImpl != null) {
                fallImpl.setRadius(radius);
            }
        }
    }

    @Override
    @NonNull
    public Bitmap blur(@NonNull Bitmap bitmap) {
        node.discardDisplayList();
        if (radius > 0) {

            int nextHeight = bitmap.getHeight();
            int nextWidth = bitmap.getWidth();
            if (nextHeight != height || nextWidth != width) {
                height = nextHeight;
                width = nextWidth;
                node.setPosition(0, 0, width, height);
            }
            Canvas canvas = node.beginRecording();
            canvas.drawBitmap(bitmap, 0, 0, null);
            node.endRecording();
        }

        // returning not blurred bitmap, because the rendering relies on the RenderNode
        return bitmap;
    }

    @Override
    public void free() {
        node.discardDisplayList();
        if (fallImpl != null) {
            fallImpl.free();
        }
    }

    @Override
    public void render(@NonNull Canvas canvas, @NonNull Bitmap bitmap) {
        //drawRenderNode requires a hardware-accelerated canvas
        if (canvas.isHardwareAccelerated()) {
            canvas.drawRenderNode(node);
            type = "RE";
        } else {
            if (fallImpl == null) {
                //noinspection deprecation
                fallImpl = new RenderScriptBlur(context);
                fallImpl.from(this);
                Log.i(BlurView.TAG, "canvas is not hardware-accelerated, created fallback RenderScriptBlur.");
            }
            fallImpl.blur(bitmap);
            fallImpl.render(canvas, bitmap);
            type = "RS";
        }
    }

    @Override
    @Nullable
    public String type() {
        return type;
    }

}
