package com.windmill.blur;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.IBinder;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;

/**
 * Basic PreDrawHelper
 * <p>
 * Blur Controller that handles all blur logic for the attached View.
 * It honors View size changes, View animation and Visibility changes.
 * <p>
 * The basic idea is to draw the view hierarchy on a bitmap, excluding the attached View,
 * then blur and draw it on the system Canvas.
 * <p>
 * It uses {@link ViewTreeObserver.OnPreDrawListener} to detect when
 * blur should be updated.
 * <p>
 */
public class PreDrawHelper extends BlurHelper {
    @NonNull
    protected final View target;
    @NonNull
    private final View blurView;
    @NonNull
    private final int[] location = new int[2];
    //private final int[] blurViewLocation = new int[2];
    //@Nullable
    private BlurCanvas internalCanvas;
    //@Nullable
    private Bitmap internalBitmap;
    private boolean initialized;
    @NonNull
    private final ViewTreeObserver.OnPreDrawListener drawListener = () -> {
        perform();
        return true;
    };

    /**
     * @param blurView View which will draw it's blurred underlying content
     * @param target   Root View where blurView's underlying content starts drawing.
     *                 Can be Activity's root content layout (android.R.id.content)
     * @param impl     sets the blur impl
     */
    public PreDrawHelper(@NonNull View blurView, @NonNull View target, @NonNull BlurImpl impl) {
        this.target = target;
        this.blurView = blurView;
        this.impl = impl;
        init();
    }

    /**
     * perform a blur change
     */
    protected void perform() {
        // Not invalidating a View here, just updating the Bitmap.
        // This relies on the HW accelerated bitmap drawing behavior in Android
        // If the bitmap was drawn on HW accelerated canvas, it holds a reference to it and on next
        // drawing pass the updated content of the bitmap will be rendered on the screen
        updateBlur();
    }

    private void init() {

        setDynamic(true);

        int measuredWidth = blurView.getWidth();
        int measuredHeight = blurView.getHeight();
        Sizer sizer = new Sizer(impl.getScale());
        if (sizer.isInvalid(measuredWidth, measuredHeight)) {
            // Will be initialized later when the View reports a size change
            blurView.setWillNotDraw(true);
            return;
        }

        blurView.setWillNotDraw(false);

        int[] bitmapSize = sizer.scale(measuredWidth, measuredHeight);
        int width = bitmapSize[0];
        int height = bitmapSize[1];
        if (internalBitmap == null || internalBitmap.getHeight() != height || internalBitmap.getWidth() != width) {
            internalBitmap = Bitmap.createBitmap(width, height, impl.bitmapConfig());
        }

        if (internalCanvas == null) {
            internalCanvas = new BlurCanvas(internalBitmap);
        } else {
            internalCanvas.setBitmap(internalBitmap);
        }
        initialized = true;

        if (target.isLaidOut()) {
            IBinder blurViewWindowToken;
            if (!blurView.isLaidOut() || (blurViewWindowToken = blurView.getWindowToken()) == null || target.getWindowToken() != blurViewWindowToken) {
                // Usually it's not needed, because `onPreDraw` updates the blur anyway.
                // But it handles cases when the PreDraw listener is attached to a different Window, for example
                // when the BlurView is in a Dialog window, but the root is in the Activity.
                // Previously it was done in `draw`, but it was causing potential side effects and Jetpack Compose crashes
                updateBlur();
            }
        }

    }

    private boolean skipDraw() {
        return !enabled || !initialized;
    }

    private void updateBlur() {
        if (skipDraw()) {
            return;
        }

        internalBitmap.eraseColor(0);
        internalCanvas.save();

        target.getLocationOnScreen(location);

        int left = location[0];
        int top = location[1];

        blurView.getLocationOnScreen(location);

        left = location[0] - left;
        top = location[1] - top;

        // https://github.com/Dimezis/BlurView/issues/128
        float scaleFactorH = scaleH();
        float scaleFactorW = scaleW();

        float scaledLeftPosition = -left / scaleFactorW;
        float scaledTopPosition = -top / scaleFactorH;

        internalCanvas.translate(scaledLeftPosition, scaledTopPosition);
        internalCanvas.scale(1 / scaleFactorW, 1 / scaleFactorH);
        //internalCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        target.draw(internalCanvas);
        internalCanvas.restore();

        internalBitmap = impl.blur(internalBitmap);
        if (!impl.multiBitmap()) {
            internalCanvas.setBitmap(internalBitmap);
        }

        //blurView.invalidate();
    }

    private float scaleH() {
        return (float) blurView.getHeight() / internalBitmap.getHeight();
    }

    private float scaleW() {
        return (float) blurView.getWidth() / internalBitmap.getWidth();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (skipDraw() ||
                // Not blurring itself or other BlurViews to not cause recursive draw calls
                // Related: https://github.com/Dimezis/BlurView/issues/110
                canvas instanceof BlurCanvas) {
            return;
        }

        canvas.save();
        // https://github.com/Dimezis/BlurView/issues/128
        canvas.scale(scaleW(), scaleH());
        impl.render(canvas, internalBitmap);
        canvas.restore();

        if (overlayColor != Color.TRANSPARENT) {
            canvas.drawColor(overlayColor);
        }

    }

    @Override
    public void update() {
        init();
    }

    @Override
    public void free() {
        setDynamic(false);
        impl.free();
        initialized = false;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setDynamic(enabled);
        //blurView.invalidate();
    }

    @Override
    public void setDynamic(boolean enabled) {
        target.getViewTreeObserver().removeOnPreDrawListener(drawListener);
        if (enabled) {
            target.getViewTreeObserver().addOnPreDrawListener(drawListener);
        }
    }

    @Override
    public void setOverlayColor(int overlayColor) {
        if (this.overlayColor != overlayColor) {
            super.setOverlayColor(overlayColor);
            blurView.invalidate();
        }
    }

}
